# RFC Management System - Frontend

React-based frontend application for the RFC Management System.

## Technology Stack

- **React 18.3.1** - UI library
- **Vite 6.0.3** - Build tool and dev server
- **Modern CSS** - Styling based on the design mockup

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── LoginForm.jsx       # Login form component
│   │   └── LoginForm.css       # Login form styles
│   ├── App.jsx                  # Main app component
│   ├── App.css                  # App styles
│   └── main.jsx                 # Entry point
├── public/                      # Static assets
├── index.html                   # HTML template
├── vite.config.js              # Vite configuration
├── package.json                # Dependencies
├── Dockerfile                  # Docker build configuration
├── nginx.conf                  # Nginx configuration
└── README.md                   # This file
```

## Features

### Login Form
- User authentication based on OpenAPI specification
- Form validation according to API schema:
  - Username: required, 1-50 characters
  - Password: required, 1-100 characters
- API endpoint: `POST /user/login`
- Response handling:
  - **200 OK**: Store tokens in localStorage, show success notification
  - **400 Bad Request**: Display validation errors
  - **401 Unauthorized**: Show authentication error
  - **500 Internal Server Error**: Display server error message
- Token management:
  - Access token storage
  - Refresh token storage (if provided)
  - Token expiration tracking
- UI features:
  - Password visibility toggle
  - Loading states
  - Toast notifications
  - Responsive design

## API Integration

The frontend communicates with the backend API through a proxy configuration:
- Development: Vite dev server proxy (`/api` → `http://backend:8080`)
- Production: Nginx reverse proxy (`/api/` → `http://backend:8080/`)

### LoginRequest Schema
```json
{
  "username": "string (1-50 chars)",
  "password": "string (1-100 chars)"
}
```

### LoginResponse Schema
```json
{
  "accessToken": "string (JWT token)",
  "refreshToken": "string (optional)",
  "expiresIn": "number (seconds)",
  "refreshExpiresIn": "number (optional, seconds)",
  "tokenType": "string (e.g., 'Bearer')"
}
```

## Development

### Prerequisites
- Docker and Docker Compose (system runs exclusively in Docker)
- No local Node.js installation required

### Running with Docker Compose

1. **Start all services** (from project root):
```bash
docker-compose up -d
```

2. **View logs**:
```bash
docker-compose logs -f frontend
```

3. **Stop services**:
```bash
docker-compose down
```

4. **Rebuild after changes**:
```bash
docker-compose up -d --build frontend
```

### Accessing the Application

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Keycloak: http://localhost:8081

## Docker Configuration

### Dockerfile
Multi-stage build:
1. **Builder stage**: Install dependencies and build the React app
2. **Production stage**: Serve built files with Nginx

### Nginx Configuration
- Serves static files from `/usr/share/nginx/html`
- Proxies API requests to backend service
- Handles SPA routing (React Router)
- Enables gzip compression
- Sets security headers

## Environment Variables

No environment variables are currently required for the frontend.
API proxy configuration is handled by Vite (dev) and Nginx (prod).

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Notes

- The system is designed to run exclusively through Docker Compose
- No local development setup is required
- All API calls go through the proxy to avoid CORS issues
- Tokens are stored in localStorage for persistence
- Form validation follows OpenAPI specification constraints

## Future Enhancements

- Implement token refresh mechanism
- Add role-based routing after login
- Implement logout functionality
- Add loading skeletons
- Add unit and integration tests

