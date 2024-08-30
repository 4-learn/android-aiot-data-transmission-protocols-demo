import asyncio
import websockets

async def connect_to_websocket():
    uri = "ws://localhost:8085/ws"  # 替換為你的 WebSocket 服務器的 URI

    try:
        async with websockets.connect(uri) as websocket:
            print("Connected to WebSocket server.")

            while True:
                # 發送一條消息到服務器
                message = input("Enter message to send (type 'exit' to quit): ")
                if message.lower() == 'exit':
                    break

                await websocket.send(message)
                print(f"Sent to server: {message}")

                # 等待從服務器接收消息
                response = await websocket.recv()
                print(f"Received from server: {response}")

    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    asyncio.run(connect_to_websocket())
