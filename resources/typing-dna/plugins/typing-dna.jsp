<%@ page import="java.io.File" %>

<%
    File tPattern = new File(getServletContext().getRealPath("js/typingdna.js"));
    if (tPattern.exists()) {
%>
	<script src="${pageContext.request.contextPath}/js/typingdna.js"></script>
<% }else{
%>
	<script src="https://www.typingdna.com/scripts/typingdna.js"></script>
<% } %>
<script>

var tdna = new TypingDNA();
tdna.addTarget("usernameUserInput");
tdna.addTarget("password");

$(document).ready(function(){
        //document.getElementById("loginForm").onsubmit = function() {getTypingPatterns()};
        //document.getElementById("loginForm").setAttribute("onsubmit", "getTypingPatterns()");
        document.getElementById("loginForm").addEventListener("submit",getTypingPatterns);
        document.getElementById("usernameUserInput").setAttribute("autocomplete", "off");
});

function getTypingPatterns() {

            var tp1 = tdna.getTypingPattern({type:1});

            //adding a new field to form
            var tp = document.createElement("input");
            tp.setAttribute("type", "hidden");
            tp.setAttribute("name", "typingPattern");
            tp.setAttribute("id", "typingPattern");
            tp.setAttribute("value", tp1 );
            document.getElementById("loginForm").append(tp);

            return true;
        }
</script>


