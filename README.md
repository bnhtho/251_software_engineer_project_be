## CREATE DATABASE BY USING DOCKER:
- Step 1: Open Terminal in this directory
- Step 2: docker compose up -d
- Step 3: docker exec -it tutor_system mysql - root -p

## CONNECT DATABASE BY USING DBEAVER
-   Step 1: Open DBeaver -> New Connection --> Choose MySQL
-   Step 2: Change port from 3306 to 3307
-   Step 3: Click at Driver properties, find allowPublicKeyRetrieval, change from false to true 