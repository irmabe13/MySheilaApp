import { Navigate } from "react-router-dom";
import type {JSX} from "react";

export default function PrivateRoute({ children }: { children: JSX.Element }) {
    const isAuth = !!localStorage.getItem("accessToken");
    return isAuth ? children : <Navigate to="/login" replace />;
}