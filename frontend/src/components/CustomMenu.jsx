import { Button, Menu, Portal } from "@chakra-ui/react";

const CustomMenu = ({ items, title }) => {
  return (
    <Menu.Root>
      <Menu.Trigger asChild>
        <Button shadow={"sm"} variant="outline" size="sm">
          { title }
        </Button>
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content>
            {items &&
              items.map((item) => (
                <Menu.Item
                  key={item.value}
                  value={item.value}
                  onClick={() => {
                    item.handler && item.handler();
                  }}
                >
                  {item.label}
                </Menu.Item>
              ))}
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
};

export default CustomMenu;
