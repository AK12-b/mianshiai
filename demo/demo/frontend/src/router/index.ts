import { createRouter, createWebHashHistory } from "vue-router";
import LandingView from "../views/LandingView.vue";
import AuthView from "../views/AuthView.vue";
import AppLayout from "../views/AppLayout.vue";
import DashboardView from "../views/DashboardView.vue";
import InterviewView from "../views/InterviewView.vue";
import PracticeView from "../views/PracticeView.vue";
import WrongBookView from "../views/WrongBookView.vue";
import ResumeView from "../views/ResumeView.vue";
import ResourcesView from "../views/ResourcesView.vue";
import UserCenterView from "../views/UserCenterView.vue";
import GrowthDataView from "../views/GrowthDataView.vue";
import SettingsView from "../views/SettingsView.vue";
import InterviewReportView from "../views/InterviewReportView.vue";
import InterviewSessionView from "../views/InterviewSessionView.vue";
import PostMatchView from "../views/PostMatchView.vue";
import PracticeSessionView from "../views/PracticeSessionView.vue";
import PracticeReportView from "../views/PracticeReportView.vue";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: "/", component: LandingView },
    { path: "/auth", component: AuthView },
    {
      path: "/app",
      component: AppLayout,
      children: [
        { path: "", redirect: "/app/dashboard" },
        { path: "dashboard", component: DashboardView },
        { path: "interview", component: InterviewView },
        { path: "practice", component: PracticeView },
        { path: "practice-session/:singleId", component: PracticeSessionView },
        { path: "practice-report/:singleId", component: PracticeReportView },
        { path: "wrong-book", component: WrongBookView },
        { path: "resume", component: ResumeView },
        { path: "resources", component: ResourcesView },
        { path: "growth-data", component: GrowthDataView },
        { path: "post-match", component: PostMatchView },
        { path: "profile", component: UserCenterView },
        { path: "settings", component: SettingsView },
        { path: "user-center", redirect: "/app/profile" },
        { path: "interview-reports", component: InterviewReportView },
        { path: "interview-session/:interviewId", component: InterviewSessionView }
      ]
    }
  ]
});

export default router;

