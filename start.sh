#!/bin/bash
clear
echo "🚀 Starting Buy 01 Microservices Ecosystem..."

# Clean old logs
echo "🧹 Clearing old log files..."
rm -rf logs
mkdir -p logs

# Stop everything on Ctrl+C
cleanup() {
    echo ""
    echo "🛑 Shutting down all microservices..."
    pkill -P $$
    echo "✅ All services stopped safely."
    exit 0
}

trap cleanup SIGINT

# All services
ALL_SERVICES=("discovery-server" "api-gateway" "user-service" "product-service" "media-service")

echo "🔨 Cleaning and rebuilding all services..."

for SERVICE in "${ALL_SERVICES[@]}"; do
    echo "🔧 Building $SERVICE..."
    cd "backend/$SERVICE" || exit
    ./mvnw clean package -DskipTests > "../../logs/${SERVICE}-build.log" 2>&1
    if [ $? -ne 0 ]; then
        echo "❌ Build failed for $SERVICE. Check logs/${SERVICE}-build.log"
        exit 1
    fi
    cd ../..
done

echo "✅ All services built successfully."

# Start Eureka first
echo "📡 Starting discovery-server..."
cd backend/discovery-server
./mvnw spring-boot:run > ../../logs/discovery-server.log 2>&1 &
cd ../..

echo "⏳ Waiting 15 seconds for Eureka..."
sleep 15

# Start other services
SERVICES=("api-gateway" "user-service" "product-service" "media-service")

for SERVICE in "${SERVICES[@]}"; do
    echo "⚙️ Starting $SERVICE..."
    cd "backend/$SERVICE"
    ./mvnw spring-boot:run > "../../logs/$SERVICE.log" 2>&1 &
    cd ../..
done

echo ""
echo "✅ All services started in background."
echo "📊 Eureka Dashboard: http://localhost:8761"
echo ""
echo "📄 Logs location:"
echo "   logs/"
echo ""
echo "🛑 Press Ctrl+C to stop everything."

wait