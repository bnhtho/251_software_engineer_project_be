## How to setup Docker on Ubuntu
```bash
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```
## CREATE DATABASE BY USING DOCKER:
- Step 1: Open Terminal in this directory
- Step 2: docker compose up -d
- Step 3: sudo docker exec -it tutor_system mysql - root -p
- **Note:** Password: admin123
## CONNECT DATABASE BY USING DBEAVER
-   Step 1: Open DBeaver -> New Connection --> Choose MySQL
-   Step 2: Change port from 3306 to 3307
-   Step 3: Click at Driver properties, find allowPublicKeyRetrieval, change from false to true 
