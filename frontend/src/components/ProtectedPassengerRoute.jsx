import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getUserRole } from "../services/authService";

/**
 * Componente de proteção de rota.
 * @param {React.ReactNode} children - O conteúdo da rota protegida.
 */
const ProtectedPassengerRoute = ({ children }) => {
  const { userToken } = useAuth();
  const isPassenger = getUserRole(userToken) === "PASSENGER";

  if (!userToken) {
    return <Navigate to="/login" replace />;
  }
  if (!isPassenger) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedPassengerRoute;
