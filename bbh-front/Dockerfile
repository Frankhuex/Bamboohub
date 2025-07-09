# bbh-front/Dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY ./nginx/bbh-front.conf /etc/nginx/conf.d/default.conf
COPY ./nginx/localhost/localhost.crt /etc/nginx/ssl/localhost.crt
COPY ./nginx/localhost/localhost.key /etc/nginx/ssl/localhost.key

EXPOSE 443
EXPOSE 80