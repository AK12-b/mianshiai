import { defineStore } from "pinia";

type StoredUser = { userId: number; name: string; role?: string };

function readStoredUser(): StoredUser | null {
  const keys = ["faceai_user", "faceai_user_session"];
  for (const k of keys) {
    try {
      const raw = k === "faceai_user" ? localStorage.getItem(k) : sessionStorage.getItem(k);
      if (!raw) continue;
      const obj = JSON.parse(raw);
      const userId = Number(obj?.userId);
      const name = String(obj?.name || "");
      if (!Number.isFinite(userId) || userId <= 0 || !name) continue;
      return { userId, name, role: obj?.role ? String(obj.role) : undefined };
    } catch {
      // ignore bad cache
    }
  }
  return null;
}

function writeStoredUser(user: StoredUser, remember: boolean) {
  const payload = JSON.stringify({ userId: user.userId, name: user.name, role: user.role || "" });
  if (remember) {
    localStorage.setItem("faceai_user", payload);
    sessionStorage.removeItem("faceai_user_session");
  } else {
    sessionStorage.setItem("faceai_user_session", payload);
    localStorage.removeItem("faceai_user");
  }
}

function clearStoredUser() {
  localStorage.removeItem("faceai_user");
  sessionStorage.removeItem("faceai_user_session");
}

function readNotifyEnabled(): boolean {
  try {
    const raw = localStorage.getItem("faceai_notify_enabled");
    if (raw == null) return true;
    return raw === "1";
  } catch {
    return true;
  }
}

function writeNotifyEnabled(enabled: boolean) {
  try {
    localStorage.setItem("faceai_notify_enabled", enabled ? "1" : "0");
  } catch {
    // ignore storage errors
  }
}

export const useAppStore = defineStore("app", {
  state: () => ({
    loading: false,
    toast: "" as string,
    notifyEnabled: readNotifyEnabled(),
    authTab: "login" as "login" | "register",
    loginForm: { email: "", password: "", remember: false },
    registerForm: { userName: "", email: "", password: "" },
    user: (() => {
      const cached = readStoredUser();
      if (cached) return { userId: cached.userId, name: cached.name, role: cached.role || "Java后端方向" };
      return { userId: null as null | number, name: "同学", role: "Java后端方向" };
    })(),
    forms: {
      interviewCreate: `{
  "userId": 1,
  "postId": 1,
  "interviewMode": 1,
  "interviewModule": "技术基础",
  "aiCharacter": 1,
  "aiGender": 2,
  "interviewDuration": 15,
  "inputType": 2
}`,
      practiceMaster: `{"userId":1,"postId":1,"knowledgePoint":"Java并发","practiceLevel":1,"taskCycle":1,"taskQuantity":10,"planStartTime":"2026-03-30T08:00:00","planEndTime":"2026-04-06T08:00:00"}`,
      wrongAdd: `{"userId":1,"questionId":1,"wrongReason":1,"knowledgePoint":"Redis缓存"}`,
      questionAdd: `{"postId":1,"questionType":1,"questionLevel":1,"questionTitle":"HashMap 与 ConcurrentHashMap 区别？","questionAnswer":"线程安全与实现机制不同","knowledgePoint":"Java并发","isAiGenerate":0}`
    },
    filters: { interviewMode: "", interviewStatus: "", wrongKeyword: "", wrongCollectedOnly: false, questionKeyword: "" },
    lists: { interviews: [] as any[], wrongs: [] as any[], questions: [] as any[] },
    pager: { interviewPage: 1, wrongPage: 1, questionPage: 1, pageSize: 6 }
  }),
  getters: {
    uid: (s) => Number(s.user.userId || 1),
    interviewFiltered: (s) =>
      s.lists.interviews.filter(
        (it) =>
          (s.filters.interviewMode === "" || String(it.interviewMode) === String(s.filters.interviewMode)) &&
          (s.filters.interviewStatus === "" ||
            (() => {
              const raw = it.interviewStatus ?? it.interviewStatusCode ?? it.status;
              const n = Number(raw);
              const st = Number.isFinite(n) ? n : 0;
              return String(st) === String(s.filters.interviewStatus);
            })())
      ),
    wrongFiltered: (s) =>
      s.lists.wrongs.filter((it) => {
        const kw = s.filters.wrongKeyword.trim();
        const kwOk = !kw || String(it.knowledgePoint || "").includes(kw);
        const cOk = !s.filters.wrongCollectedOnly || Number(it.isCollect) === 1;
        return kwOk && cOk;
      }),
    questionFiltered: (s) => {
      const kw = s.filters.questionKeyword.trim();
      if (!kw) return s.lists.questions;
      return s.lists.questions.filter(
        (q) => String(q.questionTitle || "").includes(kw) || String(q.knowledgePoint || "").includes(kw)
      );
    }
  },
  actions: {
    showToast(text: string) {
      if (!this.notifyEnabled) return;
      this.toast = text;
      window.setTimeout(() => (this.toast = ""), 2200);
    },
    setNotifyEnabled(enabled: boolean) {
      this.notifyEnabled = !!enabled;
      writeNotifyEnabled(this.notifyEnabled);
      if (this.notifyEnabled) {
        this.toast = "通知已开启";
        window.setTimeout(() => (this.toast = ""), 2200);
      } else {
        this.toast = "";
      }
    },
    parseJson(text: string) {
      return JSON.parse(text);
    },
    pretty(obj: any) {
      return JSON.stringify(obj, null, 2);
    },
    statusText(status: any) {
      const v = Number(status ?? 0);
      return v === 1 ? "进行中" : v === 2 ? "已完成" : v === 3 ? "已终止" : v === 4 ? "已暂停" : "待开始";
    },
    modeText(mode: any) {
      return Number(mode) === 1 ? "全流程" : "专项";
    },
    pagedList<T>(arr: T[], pageNo: number) {
      const start = (pageNo - 1) * this.pager.pageSize;
      return arr.slice(start, start + this.pager.pageSize);
    },
    totalPages(arr: any[]) {
      return Math.max(1, Math.ceil(arr.length / this.pager.pageSize));
    },
    async apiRequest(url: string, method = "GET", body: any = null) {
      const m = String(method || "GET").toUpperCase();
      let requestUrl = url;
      const options: any = { method: m };
      if (m === "GET") {
        // 全局禁用 GET 缓存，确保数据库新增/更新数据能实时显示到前端
        const sep = requestUrl.includes("?") ? "&" : "?";
        requestUrl = `${requestUrl}${sep}_t=${Date.now()}`;
        options.cache = "no-store";
      }
      if (body !== null) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
      }
      const res = await fetch(requestUrl, options);
      const text = await res.text();
      let data: any;
      try {
        data = text ? JSON.parse(text) : {};
      } catch {
        const snippet = text.replace(/\s+/g, " ").slice(0, 160);
        throw new Error(res.ok ? "服务器返回了非 JSON 数据" : `请求失败 (${res.status})：${snippet || "无正文"}`);
      }
      const hasBizCode = data != null && Object.prototype.hasOwnProperty.call(data, "code") && data.code != null;
      if (hasBizCode) {
        if (data.code == 200) return data.data;
        throw new Error(data.message || `业务错误 (code=${data.code})`);
      }
      if (typeof data.status === "number" && data.error) {
        throw new Error([data.message, data.error, data.path].filter(Boolean).join(" · "));
      }
      if (!res.ok) {
        throw new Error(`请求失败 (HTTP ${res.status})`);
      }
      return data.data !== undefined ? data.data : data;
    },
    async login() {
      if (!this.loginForm.email || !this.loginForm.password) return this.showToast("请先输入邮箱和密码");
      this.loading = true;
      try {
        const data = await this.apiRequest("/api/user/login", "POST", {
          email: this.loginForm.email,
          password: this.loginForm.password
        });
        this.user.userId = data.userId;
        this.user.name = data.userName || "同学";
        // 登录态持久化：勾选“记住账号”写入 localStorage，否则写入 sessionStorage（关闭浏览器自动失效）
        writeStoredUser({ userId: Number(this.user.userId), name: this.user.name, role: this.user.role }, !!this.loginForm.remember);
        this.showToast("登录成功");
        return true;
      } catch {
        this.showToast("登录失败，请重试");
        return false;
      } finally {
        this.loading = false;
      }
    },
    async register() {
      if (!this.registerForm.userName || !this.registerForm.email || !this.registerForm.password) {
        this.showToast("请填写用户名、邮箱和密码");
        return false;
      }
      this.loading = true;
      try {
        await this.apiRequest("/api/user/register", "POST", {
          userName: this.registerForm.userName,
          email: this.registerForm.email,
          password: this.registerForm.password
        });
        this.authTab = "login";
        this.loginForm.email = this.registerForm.email;
        this.showToast("注册成功，请登录");
        return true;
      } catch (e: any) {
        this.showToast(e?.message || "注册失败");
        return false;
      } finally {
        this.loading = false;
      }
    },
    logout() {
      this.user.userId = null;
      this.user.name = "同学";
      clearStoredUser();
    }
  }
});

