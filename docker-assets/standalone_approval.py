#!/usr/bin/python

import os
import sys
import xml.dom.minidom

def create_xa_datasource_node(dom):
    RHPAM_DB_HOST = os.getenv('RHPAM_DB_HOST')
    RHPAM_DB_PORT = os.getenv('RHPAM_DB_PORT')
    RHPAM_DATABASE = os.getenv('RHPAM_DATABASE')
    RHPAM_DB_USERNAME = os.getenv('RHPAM_DB_USERNAME')
    RHPAM_DB_PASSWORD = os.getenv('RHPAM_DB_PASSWORD')

    xa_datasource = dom.createElement("xa-datasource")
    xa_datasource.setAttribute("jndi-name", "java:/jboss/datasources/rhpam_EJBTimer")
    xa_datasource.setAttribute("pool-name", "ejb_timer-EJB_TIMER")
    xa_datasource.setAttribute("enabled", "true")
    xa_datasource.setAttribute("use-java-context", "true")

    xa_datasource_properties_node_1 = dom.createElement("xa-datasource-property")
    xa_datasource.appendChild(xa_datasource_properties_node_1)

    xa_datasource_properties_node_1.setAttribute("name", "DatabaseName")
    xa_datasource_properties_node_1_text = dom.createTextNode(RHPAM_DATABASE)
    xa_datasource_properties_node_1.appendChild(xa_datasource_properties_node_1_text)

    xa_datasource_properties_node_2 = dom.createElement("xa-datasource-property")
    xa_datasource.appendChild(xa_datasource_properties_node_2)

    xa_datasource_properties_node_2.setAttribute("name", "PortNumber")
    xa_datasource_properties_node_2_text = dom.createTextNode(RHPAM_DB_PORT)
    xa_datasource_properties_node_2.appendChild(xa_datasource_properties_node_2_text)
    
    xa_datasource_properties_node_3 = dom.createElement("xa-datasource-property")
    xa_datasource.appendChild(xa_datasource_properties_node_3)

    xa_datasource_properties_node_3.setAttribute("name", "ServerName")
    xa_datasource_properties_node_3_text = dom.createTextNode(RHPAM_DB_HOST)
    xa_datasource_properties_node_3.appendChild(xa_datasource_properties_node_3_text)

    xa_datasource_driver_node = dom.createElement("driver")
    xa_datasource.appendChild(xa_datasource_driver_node)
    xa_datasource_driver_node.appendChild(dom.createTextNode("postgresql"))

    xa_datasource_transaction_node = dom.createElement("transaction-isolation")
    xa_datasource.appendChild(xa_datasource_transaction_node)
    xa_datasource_transaction_node.appendChild(dom.createTextNode("TRANSACTION_READ_COMMITTED"))

    xa_pool_node = dom.createElement("xa-pool")
    xa_datasource.appendChild(xa_pool_node)

    min_pool_size_node = dom.createElement("min-pool-size")
    xa_pool_node.appendChild(min_pool_size_node)
    min_pool_size_node.appendChild(dom.createTextNode("10"))

    max_pool_size_node = dom.createElement("max-pool-size")
    xa_pool_node.appendChild(max_pool_size_node)
    max_pool_size_node.appendChild(dom.createTextNode("10"))

    security_node = dom.createElement("security")
    xa_datasource.appendChild(security_node)

    user_name_node = dom.createElement("user-name")
    security_node.appendChild(user_name_node)
    user_name_node.appendChild(dom.createTextNode("{}".format(RHPAM_DB_USERNAME)))

    password_node = dom.createElement("password")
    security_node.appendChild(password_node)
    password_node.appendChild(dom.createTextNode("{}".format(RHPAM_DB_PASSWORD)))

    validation_node = dom.createElement("validation")
    xa_datasource.appendChild(validation_node)

    validation_on_match_node = dom.createElement("validate-on-match")
    validation_node.appendChild(validation_on_match_node)
    validation_on_match_node.appendChild(dom.createTextNode("true"))

    background_validation_node = dom.createElement("background-validation")
    validation_node.appendChild(background_validation_node)
    background_validation_node.appendChild(dom.createTextNode("false"))

    valid_connection_checker_node = dom.createElement("valid-connection-checker")
    validation_node.appendChild(valid_connection_checker_node)
    valid_connection_checker_node.setAttribute("class-name", "org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker")

    exception_sorter_node = dom.createElement("exception-sorter")
    validation_node.appendChild(exception_sorter_node)
    exception_sorter_node.setAttribute("class-name", "org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter")

    return xa_datasource

def create_datasource_node(dom):
    RHPAM_DB_HOST = os.getenv('RHPAM_DB_HOST')
    RHPAM_DB_PORT = os.getenv('RHPAM_DB_PORT')
    RHPAM_DATABASE = os.getenv('RHPAM_DATABASE')
    RHPAM_DB_USERNAME = os.getenv('RHPAM_DB_USERNAME')
    RHPAM_DB_PASSWORD = os.getenv('RHPAM_DB_PASSWORD')
    PGSSLMODE = os.getenv('PGSSLMODE')
    PGSSLROOTCERT = os.getenv('PGSSLROOTCERT')

    datasource = dom.createElement("datasource")
    datasource.setAttribute("pool-name", "rhpam-RHPAM")
    datasource.setAttribute("jta", "true")
    datasource.setAttribute("jndi-name", "java:/jboss/datasources/rhpam")
    datasource.setAttribute("enabled", "true")
    datasource.setAttribute("use-java-context", "true")

    connection_url_element = dom.createElement("connection-url")
    if PGSSLROOTCERT == None:
        db_url = "jdbc:postgresql://{}:{}/{}".format(RHPAM_DB_HOST, RHPAM_DB_PORT, RHPAM_DATABASE)
    else:
        db_url = "jdbc:postgresql://{}:{}/{}?ssl=true&sslmode={}&sslrootcert={}".format(RHPAM_DB_HOST, RHPAM_DB_PORT, RHPAM_DATABASE, PGSSLMODE, PGSSLROOTCERT)

    connection_url_text = dom.createTextNode(db_url)
    connection_url_element.appendChild(connection_url_text)

    driver_element = dom.createElement("driver")
    driver_text = dom.createTextNode("postgresql")
    driver_element.appendChild(driver_text)

    security_element = dom.createElement("security")
    datasource.appendChild(security_element)
    username_element = dom.createElement("user-name")
    username_text = dom.createTextNode("{}".format(RHPAM_DB_USERNAME))
    username_element.appendChild(username_text)
    password_element = dom.createElement("password")
    password_text = dom.createTextNode("{}".format(RHPAM_DB_PASSWORD))
    password_element.appendChild(password_text)
    security_element.appendChild(username_element)
    security_element.appendChild(password_element)

    validation = dom.createElement("validation")
    validate_on_match_element = dom.createElement("validate-on-match")
    true_text = dom.createTextNode("true")
    validate_on_match_element.appendChild(true_text)
    background_validation_element = dom.createElement("background-validation")
    false_text = dom.createTextNode("false")
    background_validation_element.appendChild(false_text)
    valid_connection_checker_element = dom.createElement("valid-connection-checker")
    valid_connection_checker_element.setAttribute("class-name", "org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker")
    exception_sorter_element = dom.createElement("exception-sorter")
    exception_sorter_element.setAttribute("class-name", "org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter")
    validation.appendChild(validate_on_match_element)
    validation.appendChild(background_validation_element)
    validation.appendChild(valid_connection_checker_element)
    validation.appendChild(exception_sorter_element)

    datasource.appendChild(connection_url_element)
    datasource.appendChild(driver_element)
    datasource.appendChild(validation)

    return datasource
    
def create_system_properties_node(dom):
    KEYSTORE_PASSWORD = os.getenv('KEYSTORE_PASSWORD')

    system_properties = dom.createElement("system-properties")
    system_properties_element_1 = dom.createElement("property")
    system_properties_element_1.setAttribute("name", "javax.net.ssl.trustStore")
    system_properties_element_1.setAttribute("value", "${jboss.server.config.dir}/backoffice.jks")
    system_properties_element_2 = dom.createElement("property")
    system_properties_element_2.setAttribute("name", "javax.net.ssl.trustStorePassword")
    system_properties_element_2.setAttribute("value", "{}".format(KEYSTORE_PASSWORD))

    system_properties.appendChild(system_properties_element_1)
    system_properties.appendChild(system_properties_element_2)

    return system_properties

def update_system_properties(dom):
    server = dom.getElementsByTagName("server").item(0)
    management = dom.getElementsByTagName("management").item(0)
    server.insertBefore(create_system_properties_node(dom), management)

def update_socket_binding_node(dom):
    JBOSS_WEB_PORT = os.getenv('JBOSS_WEB_PORT')

    socket_binding_node = dom.createElement("socket-binding")
    socket_binding_node.setAttribute("interface", "bindall")
    socket_binding_node.setAttribute("name", "http")
    socket_binding_node.setAttribute("port", "${jboss.http.port:%s}" % (JBOSS_WEB_PORT))

    socket_binding_group = dom.getElementsByTagName("socket-binding-group").item(0)
    for node in socket_binding_group.childNodes:
        if node.nodeType == node.ELEMENT_NODE and node.getAttribute("name") == "http":
            socket_binding_group.replaceChild(socket_binding_node, node)

def update_datasource_node(dom):
    datasources = dom.getElementsByTagName("datasources").item(0)
    found_datasource = False
    found_xa_datasource = False

    for node in datasources.childNodes:
        if node.nodeType == node.ELEMENT_NODE and node.getAttribute("pool-name") == "rhpam-RHPAM":
            found_datasource = True
            datasources.replaceChild(create_datasource_node(dom), node)

        if node.nodeType == node.ELEMENT_NODE and node.getAttribute("pool-name") == "ejb_timer-EJB_TIMER":
            found_xa_datasource = True
            datasources.replaceChild(create_xa_datasource_node(dom), node)

    if not found_xa_datasource:
        datasources.appendChild(create_xa_datasource_node(dom))

    if not found_datasource:
        datasources.appendChild(create_datasource_node(dom))

args = sys.argv[1:]
conf = args.pop()

doc = xml.dom.minidom.parse(conf)

update_system_properties(doc)
update_socket_binding_node(doc)
update_datasource_node(doc)

with open(conf,'w') as f:
    f.write(doc.toxml())
