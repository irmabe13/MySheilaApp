import {Route, Routes} from "react-router-dom";
import PrivateRoute from "../src/router/PrivateRoute";
import Dashboard from "../src/pages/Dashboard";
import AuthPage from "../src/pages/AuthPage";
import OnboardingPage from "../src/pages/OnboardingPage";


export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<AuthPage/>}/>
            <Route path="/onboarding" element={<OnboardingPage/>}/>
            <Route path="/dashboard" element={<PrivateRoute><Dashboard/></PrivateRoute>}/>
            <Route path="*" element={<PrivateRoute><Dashboard/></PrivateRoute>}/>
        </Routes>
    );
}