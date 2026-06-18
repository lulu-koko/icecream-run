const ASSET_PATH = "./assets/";
const TARGET_DISTANCE_M = 1000;
const MAX_BRAIN_FREEZE = 100;
const MAX_BANANA_HITS = 3;
const MAX_DT_SECONDS = 0.033;

const BASE_SPEED_MPS = 32;
const BOOST_MULTIPLIER = 1.6;
const BOOST_MS = 1700;
const BRAIN_FREEZE_DECAY_PER_SECOND = 6;
const ICE_CREAM_BRAIN_FREEZE = 32;

const ITEM_SPEED_PX_PER_SECOND = 235;
const PLAYER_GRAVITY_PX_PER_SECOND2 = 1050;
const PLAYER_JUMP_POWER_PX_PER_SECOND = -575;
const GROUND_SCROLL_PX_PER_SECOND = 80;

const userIdInput = document.querySelector("#userIdInput");
const userIdError = document.querySelector("#userIdError");
const homeScreen = document.querySelector("#homeScreen");
const gameScreen = document.querySelector("#gameScreen");
const resultPanel = document.querySelector("#resultPanel");
const resultTitle = document.querySelector("#resultTitle");
const resultText = document.querySelector("#resultText");
const resultStats = document.querySelector("#resultStats");
const scoreStatus = document.querySelector("#scoreStatus");
const startButton = document.querySelector("#startButton");
const helpButton = document.querySelector("#helpButton");
const rankButton = document.querySelector("#rankButton");
const restartButton = document.querySelector("#restartButton");
const homeButton = document.querySelector("#homeButton");
const helpDialog = document.querySelector("#helpDialog");
const rankDialog = document.querySelector("#rankDialog");
const rankList = document.querySelector("#rankList");
const emptyRank = document.querySelector("#emptyRank");
const distanceText = document.querySelector("#distanceText");
const timerText = document.querySelector("#timerText");
const brainText = document.querySelector("#brainText");
const bananaText = document.querySelector("#bananaText");
const iceText = document.querySelector("#iceText");
const gameCanvas = document.querySelector("#gameCanvas");
const gameCtx = gameCanvas.getContext("2d");
const snowCanvas = document.querySelector("#snowCanvas");
const snowCtx = snowCanvas.getContext("2d");

const images = {
  player: [],
  icecream: loadImage(`${ASSET_PATH}icecream.png`),
  banana: loadImage(`${ASSET_PATH}banana.png`)
};

for (let index = 0; index < 8; index += 1) {
  images.player.push(loadImage(`${ASSET_PATH}player_run_${index}.png`));
}

let animationId = 0;
let lastTime = 0;
let lastSnowTime = 0;
let nextBananaIn = 0;
let nextIceIn = 0;
let state = createState();
let snowflakes = [];

userIdInput.value = localStorage.getItem("icecream-run-user-id") || "";

function loadImage(src) {
  const image = new Image();
  image.src = src;
  return image;
}

function createState() {
  return {
    running: false,
    ended: false,
    userId: "",
    startTime: 0,
    endTime: 0,
    durationMs: 0,
    distance: 0,
    brain: 0,
    maxBrain: 0,
    bananaHits: 0,
    iceCount: 0,
    boostUntil: 0,
    prompt: true,
    groundOffset: 0,
    player: {
      x: 112,
      yOffset: 0,
      velocityY: 0,
      width: 46,
      height: 70
    },
    bananas: [],
    icecreams: []
  };
}

function showHome() {
  cancelAnimationFrame(animationId);
  state.running = false;
  homeScreen.classList.remove("is-hidden");
  gameScreen.classList.add("is-hidden");
  resultPanel.classList.add("is-hidden");
}

function startGame() {
  const userId = userIdInput.value.trim();
  if (!userId) {
    userIdError.textContent = "请先输入用户 ID。";
    userIdInput.focus();
    return;
  }

  userIdError.textContent = "";
  localStorage.setItem("icecream-run-user-id", userId);
  state = createState();
  state.userId = userId;
  state.startTime = performance.now();
  lastTime = state.startTime;
  nextBananaIn = 0.85;
  nextIceIn = 1.25;
  state.running = true;
  homeScreen.classList.add("is-hidden");
  gameScreen.classList.remove("is-hidden");
  resultPanel.classList.add("is-hidden");
  scoreStatus.textContent = "";
  cancelAnimationFrame(animationId);
  animationId = requestAnimationFrame(gameLoop);
}

function gameLoop(now) {
  const deltaTime = Math.min((now - lastTime) / 1000, MAX_DT_SECONDS);
  lastTime = now;
  update(deltaTime, now);
  draw(now);
  if (state.running) {
    animationId = requestAnimationFrame(gameLoop);
  }
}

function update(deltaTime, now) {
  const speedMultiplier = now < state.boostUntil ? BOOST_MULTIPLIER : 1;
  const player = state.player;

  player.yOffset += player.velocityY * deltaTime;
  player.velocityY += PLAYER_GRAVITY_PX_PER_SECOND2 * deltaTime;
  if (player.yOffset > 0) {
    player.yOffset = 0;
    player.velocityY = 0;
  }

  state.groundOffset = (state.groundOffset + GROUND_SCROLL_PX_PER_SECOND * speedMultiplier * deltaTime) % 72;
  state.distance += BASE_SPEED_MPS * speedMultiplier * deltaTime;
  state.durationMs = Math.max(0, Math.round(now - state.startTime));
  state.brain = Math.max(0, state.brain - BRAIN_FREEZE_DECAY_PER_SECOND * deltaTime);
  state.maxBrain = Math.max(state.maxBrain, state.brain);

  nextBananaIn -= deltaTime;
  nextIceIn -= deltaTime;
  if (nextBananaIn <= 0) {
    state.bananas.push({ x: gameCanvas.width + 40, width: 52, height: 24 });
    nextBananaIn = randomBetween(0.95, 1.85);
  }
  if (nextIceIn <= 0) {
    state.icecreams.push({ x: gameCanvas.width + 40, width: 34, height: 48 });
    nextIceIn = randomBetween(1.25, 2.4);
  }

  const movePixels = ITEM_SPEED_PX_PER_SECOND * speedMultiplier * deltaTime;
  state.bananas.forEach((item) => {
    item.x -= movePixels;
  });
  state.icecreams.forEach((item) => {
    item.x -= movePixels;
  });
  state.bananas = state.bananas.filter((item) => item.x > -80);
  state.icecreams = state.icecreams.filter((item) => item.x > -80);

  checkCollisions(now);
  updateHud();

  if (state.brain >= MAX_BRAIN_FREEZE) {
    finishGame(false, "脑冻值爆表，雪糕今天赢了。");
  } else if (state.bananaHits >= MAX_BANANA_HITS) {
    finishGame(false, "香蕉皮连环摔，跑步姿势需要重修。");
  } else if (state.distance >= TARGET_DISTANCE_M) {
    finishGame(true, "你真的跑过来了，雪糕老板沉默了。");
  }
}

function checkCollisions(now) {
  const groundY = getGroundY();
  const playerBox = {
    x: state.player.x + 10,
    y: groundY - state.player.height + state.player.yOffset + 8,
    width: state.player.width - 10,
    height: state.player.height - 10
  };

  state.bananas = state.bananas.filter((banana) => {
    const hit = isColliding(playerBox, {
      x: banana.x,
      y: groundY - banana.height + 4,
      width: banana.width,
      height: banana.height
    });
    if (hit) {
      state.bananaHits += 1;
    }
    return !hit;
  });

  state.icecreams = state.icecreams.filter((ice) => {
    const hit = isColliding(playerBox, {
      x: ice.x,
      y: groundY - 142,
      width: ice.width,
      height: ice.height
    });
    if (hit) {
      state.iceCount += 1;
      state.brain = Math.min(MAX_BRAIN_FREEZE, state.brain + ICE_CREAM_BRAIN_FREEZE);
      state.maxBrain = Math.max(state.maxBrain, state.brain);
      state.boostUntil = now + BOOST_MS;
    }
    return !hit;
  });
}

function isColliding(a, b) {
  return a.x < b.x + b.width
    && a.x + a.width > b.x
    && a.y < b.y + b.height
    && a.y + a.height > b.y;
}

function jump() {
  if (!state.running) {
    return;
  }
  state.prompt = false;
  if (state.player.yOffset === 0) {
    state.player.velocityY = PLAYER_JUMP_POWER_PX_PER_SECOND;
  }
}

function finishGame(won, message) {
  if (state.ended) {
    return;
  }
  state.running = false;
  state.ended = true;
  state.endTime = performance.now();
  state.durationMs = Math.max(1, Math.round(state.endTime - state.startTime));
  cancelAnimationFrame(animationId);
  draw(state.endTime);

  resultTitle.textContent = won ? "通关成功" : "挑战失败";
  resultText.textContent = message;
  resultStats.innerHTML = "";
  [
    `距离 ${Math.floor(Math.min(state.distance, TARGET_DISTANCE_M))} m`,
    `用时 ${formatDuration(state.durationMs)}`,
    `雪糕 ${state.iceCount} 支`,
    `香蕉 ${state.bananaHits} / ${MAX_BANANA_HITS}`,
    `最高脑冻 ${Math.floor(state.maxBrain)}`,
    `玩家 ${state.userId}`
  ].forEach((text) => {
    const item = document.createElement("span");
    item.textContent = text;
    resultStats.appendChild(item);
  });

  resultPanel.classList.remove("is-hidden");
  submitScore(won);
}

async function submitScore(completed) {
  scoreStatus.textContent = "正在保存成绩...";
  try {
    const response = await fetch("/api/scores", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: state.userId,
        completed,
        durationMs: state.durationMs,
        iceCreamCount: state.iceCount,
        bananaHits: state.bananaHits,
        maxBrainFreeze: Math.floor(state.maxBrain),
        finalDistance: Math.floor(Math.min(state.distance, TARGET_DISTANCE_M))
      })
    });
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    scoreStatus.textContent = "成绩已保存到排行榜。";
  } catch (error) {
    scoreStatus.textContent = "成绩保存失败，请确认已通过 Spring Boot jar 运行。";
    console.error(error);
  }
}

function updateHud() {
  distanceText.textContent = `${Math.floor(Math.min(state.distance, TARGET_DISTANCE_M))} / ${TARGET_DISTANCE_M} m`;
  timerText.textContent = `用时 ${formatDuration(state.durationMs)}`;
  brainText.textContent = `脑冻 ${Math.floor(state.brain)} / ${MAX_BRAIN_FREEZE}`;
  bananaText.textContent = `香蕉 ${state.bananaHits} / ${MAX_BANANA_HITS}`;
  iceText.textContent = `雪糕 ${state.iceCount}`;
}

function draw(now) {
  const width = gameCanvas.width;
  const height = gameCanvas.height;
  const groundY = getGroundY();
  gameCtx.clearRect(0, 0, width, height);
  drawGameBackground(width, height, groundY);
  drawItems(groundY);
  drawPlayer(groundY, now);

  if (state.prompt) {
    drawOutlinedText("按空格 / 点击跳跃", width / 2, 142, 24, true);
  }
}

function getGroundY() {
  return gameCanvas.height - 112;
}

function drawGameBackground(width, height, groundY) {
  const gradient = gameCtx.createLinearGradient(0, 0, 0, height);
  gradient.addColorStop(0, "#e9fbff");
  gradient.addColorStop(0.72, "#b9edf8");
  gradient.addColorStop(1, "#ffffff");
  gameCtx.fillStyle = gradient;
  gameCtx.fillRect(0, 0, width, height);

  drawCloud(80, 112, 106);
  drawCloud(520, 86, 84);
  drawMountain(20, groundY + 10, 134, 190);
  drawMountain(740, groundY + 10, 150, 210);

  for (let x = -72 - state.groundOffset; x < width + 72; x += 72) {
    drawIceBrick(x, groundY, 72, 42);
    drawIceBrick(x + 36, groundY + 42, 72, 42);
    drawIceBrick(x, groundY + 84, 72, 42);
  }

  gameCtx.strokeStyle = "#27475d";
  gameCtx.lineWidth = 4;
  gameCtx.beginPath();
  gameCtx.moveTo(0, groundY);
  gameCtx.lineTo(width, groundY);
  gameCtx.stroke();
}

function drawCloud(x, y, size) {
  gameCtx.fillStyle = "rgba(255, 255, 255, 0.78)";
  gameCtx.beginPath();
  gameCtx.ellipse(x, y, size * 0.45, size * 0.2, 0, 0, Math.PI * 2);
  gameCtx.ellipse(x + size * 0.28, y - size * 0.12, size * 0.28, size * 0.22, 0, 0, Math.PI * 2);
  gameCtx.ellipse(x + size * 0.54, y, size * 0.28, size * 0.17, 0, 0, Math.PI * 2);
  gameCtx.fill();
}

function drawMountain(x, baseY, width, height) {
  gameCtx.fillStyle = "#b7e5ee";
  gameCtx.beginPath();
  gameCtx.moveTo(x, baseY);
  gameCtx.lineTo(x + width / 2, baseY - height);
  gameCtx.lineTo(x + width, baseY);
  gameCtx.closePath();
  gameCtx.fill();
  gameCtx.fillStyle = "#fff";
  gameCtx.beginPath();
  gameCtx.moveTo(x + width / 2, baseY - height);
  gameCtx.lineTo(x + width * 0.36, baseY - height + 58);
  gameCtx.lineTo(x + width / 2, baseY - height + 42);
  gameCtx.lineTo(x + width * 0.65, baseY - height + 58);
  gameCtx.closePath();
  gameCtx.fill();
}

function drawIceBrick(x, y, width, height) {
  gameCtx.fillStyle = "#a8e9f5";
  gameCtx.strokeStyle = "rgba(39, 71, 93, 0.65)";
  gameCtx.lineWidth = 2;
  gameCtx.beginPath();
  gameCtx.roundRect(x + 2, y + 2, width - 4, height - 4, 8);
  gameCtx.fill();
  gameCtx.stroke();
  gameCtx.strokeStyle = "rgba(255, 255, 255, 0.82)";
  gameCtx.beginPath();
  gameCtx.moveTo(x + 12, y + 12);
  gameCtx.lineTo(x + width - 14, y + 8);
  gameCtx.stroke();
}

function drawItems(groundY) {
  state.bananas.forEach((banana) => {
    if (images.banana.complete) {
      gameCtx.drawImage(images.banana, banana.x - 10, groundY - 45, 74, 52);
      return;
    }
    gameCtx.strokeStyle = "#6f4e22";
    gameCtx.lineWidth = 5;
    gameCtx.beginPath();
    gameCtx.arc(banana.x + 26, groundY - 14, 24, Math.PI * 0.15, Math.PI * 0.92);
    gameCtx.stroke();
  });

  state.icecreams.forEach((ice) => {
    if (images.icecream.complete) {
      gameCtx.drawImage(images.icecream, ice.x - 6, groundY - 154, 50, 82);
      return;
    }
    gameCtx.fillStyle = "#ff9ec8";
    gameCtx.fillRect(ice.x, groundY - 142, ice.width, ice.height);
  });
}

function drawPlayer(groundY, now) {
  const player = state.player;
  const frame = images.player[Math.floor(now / 90) % images.player.length];
  gameCtx.fillStyle = "rgba(0, 0, 0, 0.18)";
  gameCtx.beginPath();
  gameCtx.ellipse(player.x + 24, groundY - 5, 38, 8, 0, 0, Math.PI * 2);
  gameCtx.fill();

  if (frame && frame.complete) {
    gameCtx.drawImage(frame, player.x - 22, groundY - 104 + player.yOffset, 82, 110);
    return;
  }

  gameCtx.fillStyle = "#69d8ac";
  gameCtx.strokeStyle = "#27475d";
  gameCtx.lineWidth = 4;
  gameCtx.beginPath();
  gameCtx.ellipse(player.x + 22, groundY - 42 + player.yOffset, 28, 38, 0, 0, Math.PI * 2);
  gameCtx.fill();
  gameCtx.stroke();
}

function drawOutlinedText(text, x, y, size, centered = false) {
  gameCtx.font = `900 ${size}px "Microsoft YaHei", Arial, sans-serif`;
  gameCtx.textAlign = centered ? "center" : "left";
  gameCtx.lineWidth = 6;
  gameCtx.strokeStyle = "rgba(255, 255, 255, 0.95)";
  gameCtx.strokeText(text, x, y);
  gameCtx.fillStyle = "#213647";
  gameCtx.fillText(text, x, y);
  gameCtx.textAlign = "left";
}

async function renderRanks() {
  rankList.innerHTML = "";
  emptyRank.hidden = false;
  emptyRank.textContent = "正在读取排行榜...";
  try {
    const response = await fetch("/api/leaderboard?limit=50");
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    const ranks = await response.json();
    rankList.innerHTML = "";
    emptyRank.hidden = ranks.length > 0;
    emptyRank.textContent = "暂无成绩。";
    ranks.forEach((rank) => {
      const item = document.createElement("li");
      const main = document.createElement("span");
      const meta = document.createElement("span");
      main.className = "rank-main";
      meta.className = "rank-meta";
      main.textContent = `${rank.userId} · ${rank.completed ? "已通关" : "未通关"} · ${formatDuration(rank.durationMs)}`;
      meta.textContent = `雪糕 ${rank.iceCreamCount} | 香蕉 ${rank.bananaHits} | 最高脑冻 ${rank.maxBrainFreeze} | 距离 ${rank.finalDistance} m | ${formatDate(rank.createdAt)}`;
      item.append(main, meta);
      rankList.appendChild(item);
    });
  } catch (error) {
    emptyRank.hidden = false;
    emptyRank.textContent = "排行榜读取失败，请确认后端服务正在运行。";
    console.error(error);
  }
}

function resizeSnow() {
  const ratio = window.devicePixelRatio || 1;
  snowCanvas.width = Math.floor(window.innerWidth * ratio);
  snowCanvas.height = Math.floor(window.innerHeight * ratio);
  snowCanvas.style.width = `${window.innerWidth}px`;
  snowCanvas.style.height = `${window.innerHeight}px`;
  snowCtx.setTransform(ratio, 0, 0, ratio, 0, 0);
  snowflakes = Array.from({ length: 80 }, () => ({
    x: Math.random() * window.innerWidth,
    y: Math.random() * window.innerHeight,
    size: randomBetween(2, 6),
    speed: randomBetween(22, 70),
    drift: randomBetween(-16, 20)
  }));
}

function animateSnow(now = performance.now()) {
  const deltaTime = Math.min((now - lastSnowTime) / 1000 || 0, MAX_DT_SECONDS);
  lastSnowTime = now;
  snowCtx.clearRect(0, 0, window.innerWidth, window.innerHeight);
  snowCtx.fillStyle = "rgba(255, 255, 255, 0.88)";
  snowflakes.forEach((flake) => {
    flake.y += flake.speed * deltaTime;
    flake.x += flake.drift * deltaTime;
    if (flake.y > window.innerHeight + 8) {
      flake.y = -8;
      flake.x = Math.random() * window.innerWidth;
    }
    snowCtx.beginPath();
    snowCtx.arc(flake.x, flake.y, flake.size, 0, Math.PI * 2);
    snowCtx.fill();
  });
  requestAnimationFrame(animateSnow);
}

function randomBetween(min, max) {
  return min + Math.random() * (max - min);
}

function formatDuration(durationMs) {
  return `${(durationMs / 1000).toFixed(1)} s`;
}

function formatDate(value) {
  return new Date(value).toLocaleString("zh-CN", { hour12: false });
}

startButton.addEventListener("click", startGame);
restartButton.addEventListener("click", startGame);
homeButton.addEventListener("click", showHome);
helpButton.addEventListener("click", () => helpDialog.showModal());
rankButton.addEventListener("click", () => {
  renderRanks();
  rankDialog.showModal();
});

document.querySelectorAll("[data-close]").forEach((button) => {
  button.addEventListener("click", () => button.closest("dialog").close());
});

document.querySelectorAll("dialog").forEach((dialog) => {
  dialog.addEventListener("click", (event) => {
    if (event.target === dialog) {
      dialog.close();
    }
  });
});

window.addEventListener("keydown", (event) => {
  if (event.code === "Space" || event.code === "ArrowUp") {
    event.preventDefault();
    jump();
  }
});

gameCanvas.addEventListener("pointerdown", jump);
window.addEventListener("resize", resizeSnow);
resizeSnow();
animateSnow();
updateHud();
