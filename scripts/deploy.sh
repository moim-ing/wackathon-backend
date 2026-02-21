#!/bin/bash
set -e

cd ~/wackathon-backend

git pull origin main

docker compose -f docker-compose.prod.yml pull

docker compose -f docker-compose.prod.yml up -d --remove-orphans

docker image prune -f
