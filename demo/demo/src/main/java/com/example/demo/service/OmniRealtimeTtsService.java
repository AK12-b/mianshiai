package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * 语音面试：使用百炼 CosyVoice（默认 {@code cosyvoice-v3-flash}，HTTP SpeechSynthesizer），
 * 通过 {@code voice} + {@code instruction} 控制音色与情绪；v3-flash 标杆音色要求 instruction 为官方 Instruct 句式。
 */
@Slf4j
@Service
public class OmniRealtimeTtsService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${dashscope.tts.synthesizer-url:https://dashscope.aliyuncs.com/api/v1/services/audio/tts/SpeechSynthesizer}")
    private String synthesizerUrl;

    @Value("${dashscope.tts.model:cosyvoice-v3-flash}")
    private String ttsModel;

    /**
     * @param instructions 预留；CosyVoice 使用 instruction 字段传递短情感指令（见 resolveCosyVoiceTtsParams）
     * @param lineToSpeak    待合成文本
     */
    public byte[] synthesizeInterviewerLineWav(
            String instructions, String lineToSpeak, Integer aiCharacter, Integer aiGender) throws Exception {
        if (lineToSpeak == null || lineToSpeak.isBlank()) {
            throw new IllegalArgumentException("lineToSpeak 为空");
        }
        InterviewInterviewerPromptBuilder.CosyVoiceTtsParams p =
                InterviewInterviewerPromptBuilder.resolveCosyVoiceTtsParams(aiCharacter, aiGender);
        if (log.isDebugEnabled()) {
            log.debug(
                    "CosyVoice 合成: model={}, voice={}, instructLen={}, textLen={}, instructionsLen={}",
                    ttsModel,
                    p.voice(),
                    p.instruction() != null ? p.instruction().length() : 0,
                    lineToSpeak.length(),
                    instructions != null ? instructions.length() : 0);
        }

        JSONObject input = new JSONObject();
        input.put("text", lineToSpeak);
        input.put("voice", p.voice());
        input.put("format", "wav");
        input.put("sample_rate", 24000);
        if (p.instruction() != null && !p.instruction().isBlank()) {
            input.put("instruction", p.instruction());
        }
        input.put("language_hints", new String[] {"zh"});

        JSONObject body = new JSONObject();
        body.put("model", ttsModel);
        body.put("input", input);

        HttpClient client =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(15))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(synthesizerUrl))
                        .timeout(Duration.ofSeconds(90))
                        .header("Authorization", "Bearer " + apiKey.trim())
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString(), StandardCharsets.UTF_8))
                        .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "CosyVoice HTTP " + resp.statusCode() + "：" + truncate(resp.body(), 500));
        }
        JSONObject root = JSON.parseObject(resp.body());
        if (root.containsKey("code") && !"null".equals(String.valueOf(root.get("code")))) {
            throw new IllegalStateException(
                    "CosyVoice 业务错误: " + root.getString("message") + " code=" + root.get("code"));
        }
        JSONObject output = root.getJSONObject("output");
        if (output == null) {
            throw new IllegalStateException("CosyVoice 响应缺少 output: " + truncate(resp.body(), 400));
        }
        JSONObject audio = output.getJSONObject("audio");
        if (audio == null) {
            throw new IllegalStateException("CosyVoice 响应缺少 audio");
        }
        String b64 = audio.getString("data");
        if (b64 != null && !b64.isBlank()) {
            return Base64.getDecoder().decode(b64);
        }
        String url = audio.getString("url");
        if (url != null && !url.isBlank()) {
            return downloadAudio(client, url);
        }
        throw new IllegalStateException("CosyVoice 未返回音频 data 或 url");
    }

    private static byte[] downloadAudio(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest get =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(60))
                        .GET()
                        .build();
        HttpResponse<byte[]> bin = client.send(get, HttpResponse.BodyHandlers.ofByteArray());
        if (bin.statusCode() / 100 != 2) {
            throw new IllegalStateException("下载合成音频失败 HTTP " + bin.statusCode());
        }
        return bin.body();
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.replace("\r\n", " ").replace('\n', ' ');
        return t.length() <= max ? t : t.substring(0, max) + "…";
    }
}
