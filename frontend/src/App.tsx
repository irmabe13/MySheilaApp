import {Route, Routes} from "react-router-dom";
import ProtectedRoute from "./routes/ProtectedRoute.tsx";
import Dashboard from "./pages/Dashboard";
import AuthPage from "./pages/AuthPage";
import OnboardingPage from "./pages/OnboardingPage";


export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<AuthPage/>}/>
            <Route path="/register" element={<AuthPage/>}/>
            <Route element={<ProtectedRoute/>}>
                <Route path="/onboarding" element={<OnboardingPage/>}/>
                <Route path="/dashboard" element={<Dashboard/>}/>
                <Route path="*" element={<Dashboard/>}/>
            </Route>
        </Routes>
    );
}