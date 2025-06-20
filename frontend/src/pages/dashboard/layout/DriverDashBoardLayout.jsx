import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import { getUserRole } from "../../../services/authService";
import DriverHeader from "../driver/DriverHeader";
import PassengerHeader from "../passenger/PassengerHeader";
import Footer from "./Footer";

const DriverDashBoardLayout = () => {
  return (
    <>
      <DriverHeader />
      <main>
        <Outlet />
      </main>
      <Footer />
    </>
  );
};

export default DriverDashBoardLayout;
