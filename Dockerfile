FROM registry.redhat.io/rhpam-7/rhpam73-kieserver-openshift:1.0
LABEL "io.openshift.s2i.build.image"="registry.redhat.io/rhpam-7/rhpam73-kieserver-openshift" \
      "io.openshift.s2i.scripts-url"="image:///usr/local/s2i" \
      "io.openshift.s2i.destination"="/tmp"

USER root
COPY docker-assets/entrypoint /usr/bin
COPY docker-assets/cdappconfig_parser.py /usr/bin
COPY docker-assets/standalone_approval.py /usr/bin
COPY docker-assets/standalone-approval.xml /opt/eap/standalone/configuration
COPY src /tmp/src/src
COPY pom.xml /tmp/src
RUN chown -R 1001:0 /tmp/src && touch /opt/rdsca.crt && chmod 666 /opt/rdsca.crt /opt/eap/standalone/configuration/standalone-approval.xml
USER 1001

WORKDIR "/home/jboss"
ENV M2_HOME ""

RUN /usr/local/s2i/assemble && cp -R /home/jboss/?/.m2 /home/jboss

ENTRYPOINT ["entrypoint"]
CMD ["/usr/local/s2i/run"]
