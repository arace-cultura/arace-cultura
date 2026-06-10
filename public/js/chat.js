const chatMessages = document.querySelector(".chat__messages");
const chatForm = document.querySelector(".chat__form");
const chatInput = document.querySelector(".chat__input");

// Conecta no servidor que rodou no terminal
const websocket = new WebSocket("ws://localhost:8080");

websocket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    const messageDiv = document.createElement("div");
    
    // Para saber se a mensagem é sua ou de outro
    messageDiv.classList.add(data.userId === myId ? "message--self" : "message--other");
    messageDiv.innerHTML = data.content;
    
    chatMessages.appendChild(messageDiv);
};

chatForm.onsubmit = (e) => {
    e.preventDefault();
    const msg = { userId: myId, content: chatInput.value };
    websocket.send(JSON.stringify(msg));
    chatInput.value = "";
};