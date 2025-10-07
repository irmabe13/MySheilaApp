import {Route, Routes} from "react-router-dom";
import ProtectedRoute from "./routes/ProtectedRoute.tsx";
import Dashboard from "./pages/DashboardPage";
import AuthPage from "./pages/AuthPage";
import OnboardingPage from "./pages/OnboardingPage";
import OnboardingWrapper from "./routes/OnboardingWrapper.tsx"; // Import du Wrapper

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<AuthPage/>}/>
            <Route path="/register" element={<AuthPage/>}/>
            <Route element={<ProtectedRoute/>}>
                <Route element={<OnboardingWrapper/>}>
                    <Route path="/onboarding" element={<OnboardingPage/>}/>
                    <Route path="/dashboard" element={<Dashboard/>}/>
                    <Route path="*" element={<Dashboard/>}/>
                </Route>
            </Route>
        </Routes>
    );
}
