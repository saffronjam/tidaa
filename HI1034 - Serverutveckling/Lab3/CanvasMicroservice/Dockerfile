FROM node:13.12.0-alpine

ENV PATH /node_modules/.bin:$PATH

# Install app dependencies
COPY package.json ./
COPY package-lock.json ./
RUN npm install --silent

COPY . ./

EXPOSE 5000

# start app
CMD ["npm", "start"]