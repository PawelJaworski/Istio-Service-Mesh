export NVM_DIR=~/.nvm
source ~/.nvm/nvm.sh

nvm use v10.15.3
npm install --prefix ./offer/offer-frontend/
npm run build --prefix ./offer/offer-frontend/
npm install --prefix ./loan/loan-frontend/
npm run build --prefix ./loan/loan-frontend/

mvn clean install