<%@ page import="java.io.File" %>

<%
    File tpattern = new File(getServletContext().getRealPath("plugins/typing-dna.jsp"));
    if (tpattern.exists() && !isIdentifierFirstLogin(inputType)) {
%>
        <jsp:include page="plugins/typing-dna.jsp"/>
<% }  %>