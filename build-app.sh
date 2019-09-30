export NVM_DIR=~/.nvm
source ~/.nvm/nvm.sh

nvm use v10.15.3
npm install --prefix ./offer/offer-frontend/
npm run build --prefix ./offer/offer-frontend/

mvn clean install