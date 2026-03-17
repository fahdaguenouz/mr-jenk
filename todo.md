global structure 

buy-01-ecosystem/
├── .env                        # Global environment variables
├── docker-compose.yml          # Spins up Kafka, MongoDB, Eureka, Services
├── setup.sh                    # Automation script to init databases/folders
├── README.md                   # Comprehensive documentation
│
├── frontend/                   # Angular 16+ Application
│   ├── src/
│   ├── package.json
│   └── angular.json
│
└── backend/                    # Spring Boot Microservices
    ├── discovery-server/       # Netflix Eureka
    ├── api-gateway/            # Spring Cloud Gateway
    ├── user-service/           # Port 8081: Auth, Roles, JWT
    ├── product-service/        # Port 8082: Products CRUD
    └── media-service/          # Port 8083: Image upload/validation


fron structure 
frontend/src/app/
├── core/                   # Singleton services, interceptors, guards
│   ├── guards/             # AuthGuard, RoleGuard (CLIENT vs SELLER)
│   ├── interceptors/       # JwtInterceptor, ErrorInterceptor
│   └── services/           # AuthService, TokenStorageService
│
├── shared/                 # Reusable UI components, pipes, Material modules
│   ├── components/         # e.g., Navbar, Footer, Snackbar wrappers
│   └── models/             # TypeScript interfaces (Product, User)
│
├── features/               # Lazy-loaded feature modules
│   ├── auth/               # Login & Register components
│   ├── public/             # Public product listing grid
│   └── seller/             # Seller dashboard
│       ├── product-manage/ # CRUD forms for products
│       └── media-manage/   # File upload UI (checking 2MB size locally)
│
├── app-routing.module.ts   # Main route definitions
└── app.component.ts        # Root component