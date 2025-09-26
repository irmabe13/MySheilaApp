import {Routes, Route} from "react-router-dom";
import PrivateRoute from "../src/router/PrivateRoute";
import Dashboard from "../src/pages/Dashboard";
import AuthPage from "../src/pages/AuthPage";

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<AuthPage />} />
            <Route
                path="/dashboard"
                element={<PrivateRoute><Dashboard /></PrivateRoute>}
            />
            <Route path="*" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        </Routes>
    );
}