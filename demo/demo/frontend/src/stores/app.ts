import { defineStore } from "pinia";

export const useAppStore = defineStore("app", {
  state: () => ({
    loading: false,
    toast: "" as string,
    authTab: "login" as "login" | "register",
    loginForm: { email: "", password: "", remember: false },
    registerForm: { userName: "", email: "", password: "" },
    user: { userId: null as null | number, name: "同学", role: "Java后端方向" },
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
          (s.filters.interviewStatus === "" || String(it.status) === String(s.filters.interviewStatus))
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
      this.toast = text;
      window.setTimeout(() => (this.toast = ""), 2200);
    },
    parseJson(text: string) {
      return JSON.parse(text);
    },
    pretty(obj: any) {
      return JSON.stringify(obj, null, 2);
    },
    statusText(status: any) {
      return status === 1 ? "进行中" : status === 2 ? "已结束" : status === 3 ? "已暂停" : status === 4 ? "已终止" : "待开始";
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
      const options: any = { method };
      if (body !== null) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
      }
      const res = await fetch(url, options);
      const data = await res.json();
      if (data.code !== 200) throw new Error(data.message || "请求失败");
      return data.data;
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
    }
  }
});

