import {Navigate, Outlet} from "react-router-dom";
import {getAccess} from "../lib/auth.ts";


export default function ProtectedRoute() {
    const token = getAccess();
    if (!token) return <Navigate to="/login" replace/>;
    return <Outlet/>;
}