@echo off
echo Starting AuctionSite Backend with dev profile...
set SPRING_PROFILES_ACTIVE=dev
mvnw.cmd spring-boot:run

