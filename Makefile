# Simple Makefile helpers for frontend

.PHONY: frontend-install frontend-dev frontend-build frontend-preview frontend-clean frontend-docker-build frontend-docker-run

FRONTEND_DIR := frontend

frontend-install:
	@echo "[frontend] Installing dependencies..."
	cd $(FRONTEND_DIR) && npm ci

frontend-dev:
	@echo "[frontend] Starting dev server (Vite)..."
	cd $(FRONTEND_DIR) && npm run dev -- --host

frontend-build:
	@echo "[frontend] Building..."
	cd $(FRONTEND_DIR) && npm run build

frontend-preview:
	@echo "[frontend] Previewing production build..."
	cd $(FRONTEND_DIR) && npm run preview -- --host

frontend-clean:
	@echo "[frontend] Cleaning build artifacts..."
	cd $(FRONTEND_DIR) && rm -rf node_modules dist

# Optional: run via Docker (dev Dockerfile)
frontend-docker-build:
	@echo "[frontend] Building dev Docker image..."
	docker build -t code-vibes-frontend -f $(FRONTEND_DIR)/Dockerfile.dev $(FRONTEND_DIR)

frontend-docker-run:
	@echo "[frontend] Running dev Docker container..."
	docker run --rm -it -p 5173:5173 code-vibes-frontend


