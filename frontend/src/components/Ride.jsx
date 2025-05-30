import { Avatar, Button, Card, DataList } from "@chakra-ui/react";
import { useState } from "react";
export const Ride = ({ stats, isAvailable, isDone }) => {
  let actionButton = null;
  if (!isDone) {
    actionButton = isAvailable ? (
      <Button variant="outline" colorPalette="green">
        Solicitar
      </Button>
    ) : (
      <Button variant="outline" colorPalette="red">
        Cancelar
      </Button>
    );
  }

  return (
    <Card.Root
      width="320px"
      className={isDone ? "ride-done" : ""}
    >
      <Card.Body gap="2">
        <Avatar.Root>
          <Avatar.Fallback />
        </Avatar.Root>
        <Card.Title mt="2">{stats.driverName}</Card.Title>
        <div className="ride-grid">
          <DataList.Root>
            <div className="ride-row">
              <DataList.Item>
                <DataList.ItemLabel>Modelo</DataList.ItemLabel>
                <DataList.ItemValue>
                  {stats.car.brand} {stats.car.model}
                </DataList.ItemValue>
              </DataList.Item>
              <DataList.Item>
                <DataList.ItemLabel>Placa</DataList.ItemLabel>
                <DataList.ItemValue>
                  {stats.car.licensePlate}
                </DataList.ItemValue>
              </DataList.Item>
            </div>
            <div className="ride-row">
              <DataList.Item>
                <DataList.ItemLabel>Início</DataList.ItemLabel>
                <DataList.ItemValue>{`${stats.rideDate} ${stats.rideTime}`}</DataList.ItemValue>
              </DataList.Item>
              <DataList.Item>
                <DataList.ItemLabel>Vagas Disponíveis</DataList.ItemLabel>
                <DataList.ItemValue>{stats.availableSeats}</DataList.ItemValue>
              </DataList.Item>
            </div>
            <div className="ride-row">
              <DataList.Item>
                <DataList.ItemLabel>Local de Partida</DataList.ItemLabel>
                <DataList.ItemValue>{stats.pickupLocation}</DataList.ItemValue>
              </DataList.Item>
              <DataList.Item>
                <DataList.ItemLabel>Local de Chegada</DataList.ItemLabel>
                <DataList.ItemValue>{stats.dropoffLocation}</DataList.ItemValue>
              </DataList.Item>
            </div>
          </DataList.Root>
        </div>
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            gap: "8px",
            marginTop: "8px",
          }}
        >
          {stats.passengers &&
            stats.passengers.map((passenger) => (
              <Avatar.Root
                key={passenger}
                onClick={() => console.log(passenger)}
                cursor={"pointer"}
              >
                <Avatar.Fallback name={passenger} />
              </Avatar.Root>
            ))}
        </div>
      </Card.Body>
      <Card.Footer justifyContent="flex-end">{actionButton}</Card.Footer>
    </Card.Root>
  );
};

export default Ride;
