initRequest = function(input) {
    <!--if (input === "loginUser") {
        // Using IDE to run the project with embedded database server on port 8082.
        const url = "http://localhost:8082/rest/user/v1/loginUser";

        // Using Tomcat with specific ip to the server and port 8080, and a database named squareit.
        // const url = "http://178.251.131.62:8080/rest/user/v1/loginUser";

        // Using Tomcat with localhost to the server and port 8080, and a database named squareit.
        // const url = "http://localhost:8080/squareit/rest/user/v1/loginUser";

        const userLogin = {
            "email": document.querySelector("input[id=loginUserEmail]").value,
            "password": document.querySelector("input[id=loginUserPassword]").value
        };

        sendRequest(url, userLogin);
    }-->

    if (input === "createUser") {
        // Using IDE to run the project with embedded database server on port 8082.
        const url = "http://localhost:8082/rest/user/v1/upsertUser";

        // Using Tomcat with specific ip to the server and port 8080, and a database named squareit.
        //const url = "http://178.251.131.62:8080/squareit/rest/user/v1/upsertUser";

        // Using Tomcat with localhost to the server and port 8080, and a database named squareit.
        // const url = "http://localhost:8080/squareit/rest/user/v1/upsertUser";

        const userCreate = {
            "firstName": document.querySelector("input[id=createUserFirstName]").value,
            "lastName": document.querySelector("input[id=createUserLastName]").value,
            "userName": document.querySelector("input[id=createUserUserName]").value,
            "email": document.querySelector("input[id=createUserEmail]").value,
            "password": document.querySelector("input[id=createUserPassword]").value,
            "confirmPassword": document.querySelector("input[id=createUserConfirmPassword]").value,
            "role": "USER_ROLE",
            "enabled": false
        };

        sendRequest(url, userCreate);
    }
};

sendRequest = function(url, request) {
	<!-- Hey Istvan. This is just about posting (put-ing) a user in the db. In Chrome: error + works great (does the job) in db through the system.
		In Firefox: No go. All jUnit tests: Great. Postman: Great. Observe!! -> This does not include sending an activation mail to user and such. -->
    console.log(url);
    console.log(request);
    console.log("Before the fetch");

    fetch(url, {
        method: 'PUT',
        body: JSON.stringify(request),
        headers: new Headers ({
            'Content-Type': 'application/json'
        })
    })
        .then(res => res.json())
        .then(response => console.log('Can we get a Success: ', response))
        .catch(error => console.log('My very own little network Error: ', error));

    console.log("The end of fetch");
};

checkPass = function() {
    const userPassword = document.querySelector("input[id=createUserPassword]");
    const userConfirmPassword = document.querySelector("input[id=createUserConfirmPassword]");

    const validColor = "#66cc66";
    const invalidColor = "#B22222";

    if (userPassword.value !== "" && userPassword.value === userConfirmPassword.value
        && userPassword.value.length >= 8 && userConfirmPassword.value.length >= 8) {
        userPassword.style.backgroundColor = validColor;
        userConfirmPassword.style.backgroundColor = validColor;

        return true;
    } else if (userConfirmPassword.value !== "") {
        userConfirmPassword.style.backgroundColor = invalidColor;
    } else {
        userConfirmPassword.style.backgroundColor = "black";
    }

    return false;
};

checkCreateUser = function() {
    const userFirstName = document.querySelector("input[id=createUserFirstName]");
    const userUserName = document.querySelector("input[id=createUserUserName]");
    const userEmail = document.querySelector("input[id=createUserEmail]");
    const idButton = document.querySelector("button[id=createButton]");

    buttonState(idButton, true, "btn-disable", "btn");
    checkPass();

    if (userFirstName.validity.valid && userEmail.validity.valid && userUserName.validity.valid
        && checkPass() === true && checkEmail(userEmail.value)) {
        buttonState(idButton, false, "btn", "btn-disable");
    }
};

checkLoginUser = function() {
    const loginUserEmail = document.querySelector("input[id=loginUserEmail]");
    const loginUserPassword = document.querySelector("input[id=loginUserPassword]");
    const idButton = document.querySelector("button[id=loginButton]");

    buttonState(idButton, true, "btn-disable", "btn");

    if (loginUserPassword.validity.valid && checkEmail(loginUserEmail.value)) {
        buttonState(idButton, false, "btn", "btn-disable");
    }
};

checkEmail = function(email) {
    const filter = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;

    return (filter.test(email))
};

buttonState = function(idButton, notClickable, addClass, removeClass) {
    idButton.classList.remove(removeClass);
    idButton.classList.add(addClass);
    idButton.disabled = notClickable;
};
