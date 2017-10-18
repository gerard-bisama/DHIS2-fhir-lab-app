FROM node:8

WORKDIR /usr/src/app
RUN git clone https://github.com/gerard-bisama/DHIS2-fhir-lab-app
WORKDIR /usr/src/app/DHIS2-fhir-lab-app/convert-dhi2-fhir

# sets default
ARG USER_PASS=admin:district
ENV USER_PASS ${USER_PASS}
# set argument at build time otherwise default is used
RUN sed -i "s|admin:district|${USER_PASS}|" manifest.webapp

RUN mkdir -p /home/server-hit/Documents/datalab &&\
    mkdir -p /home/server-hit/Documents/datalab/source &&\
    mkdir -p /home/server-hit/Documents/datalab/temp &&\
    mkdir -p /home/server-hit/Documents/datalab/treated &&\
    mkdir -p /home/server-hit/Documents/datalab/errors

RUN npm install

EXPOSE 8083

CMD [ "npm", "start" ]

# you must set user:pass at build time: docker build --build-arg USER_PASS="user:pass" .
# then: docker run -p 8083:8083 -d <image hash>
