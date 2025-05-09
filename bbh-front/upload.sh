rm -rf dist
npm run build | grep -v 'no-unused-vars'
scp -r ./dist frank@115.159.31.68:./bbh-0.2/bbh-front-0.2