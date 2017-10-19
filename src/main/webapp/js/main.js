
$(function() {
	$("#captcha").append("<div class=\"g-recaptcha\" data-sitekey=\"" + captchaKey + "\"></div>");
});

function callRequestAccount(username, password, firstname, lastname,
		companyname, contract) {
	$.get("query/requestAccount", {
		username : username,
		password : password,
		firstname : firstname,
		lastname : lastname,
		companyname : companyname,
		contract : contract
	}, function(response) {
		alert(response);
		$("#resetButton").click();
		grecaptcha.reset();
	}, "text");
}

function callAuthorizeAccount(username, password, adminname, adminpwd) {
	$.get("query/createAccount", {
		username : username,
		password : password,
		adminname : adminname,
		adminpwd : adminpwd
	}, function(response) {
		console.log(response);
		//$("#username").text(response);    
		SetStatusLink(username, response);
	}, "text");
}

function callValidateCaptcha(callback) {
	var gresponse = grecaptcha.getResponse();

	$.get("query/validateCaptcha", {
		gRecaptchaResponse : gresponse
	}, function(response) {
		console.log(response)
		callback(response);
	}, "text");
}

function RequestAccount() {

	callValidateCaptcha(function(verified) {

		var username = $("#email").val();
		if (isEmptyOrBlank(username)) {
			showErrorMsg('Valid Email Address is required!');
			return;
		}
		var pwd = $("#pwd").val();
		if (isEmptyOrBlank(pwd)) {
			showErrorMsg('Valid Password is required!');
			return;
		}
		var firstname = $("#fname").val();
		if (isEmptyOrBlank(firstname)) {
			showErrorMsg('Valid First Name is required!');
			return;
		}
		var lastname = $("#lname").val();
		if (isEmptyOrBlank(lastname)) {
			showErrorMsg('Valid Last Name is required!');
			return;
		}
		var companyname = $("#cname").val();
		if (isEmptyOrBlank(companyname)) {
			showErrorMsg('Valid Company Name is required!');
			return;
		}
		var contract = $("#contract").val();
	    if ( isEmptyOrBlank(contract) && !username.toLowerCase().endsWith("@dot.gov") ) {
	    	showErrorMsg('Valid USDOT Contract or Agreement is required!');
	    	return;
	    }

		if (verified == "true") {
			callRequestAccount(username.trim(), pwd.trim(), firstname.trim(),
					lastname.trim(), companyname.trim(), contract.trim());
		} else {
			alert("Complete CAPTCHA correctly to submit your request!");
			return;
		}
	});
};

function showErrorMsg(str) {
	alert(str);
	grecaptcha.reset();
}

function isEmptyOrBlank(str) {
	return (!str || 0 === str.length || /^\s*$/.test(str));
}

function AuthorizeAccount() {
	var adminname = $("#adminname").val();
	if (isEmptyOrBlank(adminname)) {
		alert('Valid Approver Email Address is required!');
		return;
	}
	var adminpwd = $("#adminpwd").val();
	if (isEmptyOrBlank(adminpwd)) {
		alert('Valid Approver Password is required!');
		return;
	}
	var username = getParameterByName("email");
	if (isEmptyOrBlank(username)) {
		alert('The URL is malformed: email parameter is not found!');
		return;
	}
	var password = getParameterByName("password");
	if (isEmptyOrBlank(password)) {
		alert('The URL is malformed: password parameter is not found!');
		return;
	}
	callAuthorizeAccount(username, password, adminname, adminpwd);
};

function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex
			.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g,
			" "));
}

function SetAccountName() {
	var username = getParameterByName("email");
	$("#username").text(username);
};

function SetStatusLink(username, status) {
	var subject;
	var body;
	if (status.charAt(0) != 'C') {
		subject = "CV UIs account has been activated";
		body = "Your request for the Connected Vehicle UIs access account has been approved."
	} else {
		subject = "CV UIs account authorization failed";
		body = "Attempt to authorize your Connected Vehicle UIs access account failed.";
	}
	var tail = "If you did not request this account please notify the sender immediately. ";
	body = escape(body + "\r\n" + tail);
	$("#username").html(
			function() {
				var link = "<a href='mailto:" + escape(username) + "?subject="
						+ escape(subject) + "&body=" + body + "'>" + status
						+ "</a>";
				return link;
			});
};
