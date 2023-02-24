const baseUrl = "http://localhost:8080";
const menuList = document.querySelectorAll("li");
const content = document.querySelector(".content");

function makeMenuActionable() {
    menuList.forEach(element => {
        element.addEventListener("click", () => {
            content.className = `content ${element.className}`;
            changeContent();
        });
    });
}

function changeContent() {
    switch(content.className) {
        case 'content upload':
            uploadForm();
            break;
        case 'content quantity':
            getQuantity();
            break;
        case 'content list':
            listOfUsers();
            break;
        case 'content oldest':
            findOldest();
            break;
        case 'content find':
            findUser();
            break;
        case 'content deleteOne':
            deleteOne();
            break;
        case 'content deleteAll':
            deleteAll();
            break;
    }
}

function uploadForm() {
    drawUploadForm();
    content.querySelector("form").addEventListener("submit", uploadFile);
}

function uploadFile() {
    event.preventDefault();
    const input = document.getElementById("myFile");
    const formData = new FormData();
    formData.append('file', input.files[0]);
    fetch(baseUrl + "/users", {
            method: "POST",
            headers: {
                'Accept': '*/*'
            },
            body: formData
        })
        .then(function(response) {
            if (!response.ok) {
                throw Error(response.statusText);
            }
            return response;
        }).then(function(response) {
            drawUploadForm();
            content.innerHTML += `<p style="color:green">Data uploaded!</p>`;
            content.querySelector("form").addEventListener("submit", uploadFile);
        }).catch(function(error) {
            drawUploadForm();
            content.innerHTML += `<p style="color:red">Problem during sending data!</p>`;
            content.querySelector("form").addEventListener("submit", uploadFile);
        });
}

function drawUploadForm() {
    content.innerHTML = `<form action="" enctype="multipart/form-data">
                            Browse csv file with data<br>
                            <input type="file" id="myFile">
                            <input type="submit">
                        </form>`;
}

function getQuantity() {
    content.innerHTML = '<p>Waiting for data!</p>';
    fetch(baseUrl + "/users?quantity")
            .then(response => response.json())
            .then((quantity) => {
                if (quantity === 0)
                    content.innerHTML = "<p>Database is empty</p>";
                else if (quantity === 1)
                    content.innerHTML = "<p>There is <b>1</b> user in database</p>";
                else
                    content.innerHTML = `<p>There is <b>${quantity}</b> users in database.</p>`;
            });
}

function userInfo(user) {
    return `<p>Id: ${user.id}<br>
            First name: ${user.firstName}<br>
            Last name: ${user.lastName}<br>
            Birth date: ${user.birthDate}<br>
            Phone number: ${user.phoneNumber}</p><br>`
}

function listOfUsers() {
    content.innerHTML = `<form action="">
                            Write number of page: 
                            <input type="number" id="page">
                            Number of users on page:
                            <input type="number" id="size">
                            <input type="submit">
                        </form>`;
    content.querySelector("form").addEventListener("submit", addingUsers);
}

function addingUsers() {
    event.preventDefault();
    const page = document.getElementById("page");
    const size = document.getElementById("size");
    if (size.value > 2) {
        content.style.height = `${500 + (180 * (size.value - 2))}px`;
        document.querySelector(".menu").style.height = `${500 + (180 * (size.value - 2))}px`;
    }
    fetch(baseUrl + `/users?all&page=${page.value}&size=${size.value}`)
            .then(response => response.json())
            .then((userArr) => {
                content.innerHTML = `
                        <form action="">
                            Page number ${page.value}<br>
                            Write number of page: 
                            <input type="number" id="page">
                            Number of users on page:
                            <input type="number" id="size">
                            <input type="submit">
                        </form>`;
                userArr.forEach(user => {
                    content.innerHTML += userInfo(user);
                });
                content.querySelector("form").addEventListener("submit", addingUsers);
            });
}

function findOldest() {
    content.innerHTML = '<p>Searching for user!</p>';
    fetch(baseUrl + "/users?oldest")
        .then(response => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                return response;
            })
            .then(response => response.json())
            .then((user) => {
                content.innerHTML = userInfo(user);
            }).catch ((error) => {
                content.innerHTML = "<p>User not found!</p>";
            });
}

function findUser() {
    content.innerHTML = `<form action="">
                            Write user's last name<br>
                            <input type="text" id="userLastName">
                            <input type="submit">
                        </form>`;
    content.querySelector("form").addEventListener("submit", function(event) {
        event.preventDefault();
        const lastName = document.getElementById("userLastName");
        content.innerHTML = '<p>Searching for user!</p>';
        fetch(baseUrl + `/users/${lastName.value}`)
                .then(response => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    return response;
                })
                .then(response => response.json())
                .then((user) => {
                    content.innerHTML = userInfo(user);
                }).catch ((error) => {
                    content.innerHTML = "<p>User not found!</p>";
                });
    });
}

function deleteAll() {
    content.innerHTML = '<p>Deleting all users data!</p>';
    fetch(baseUrl + "/users", {
                method: "DELETE"
            })
            .then(response => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    return response;
                })
            .then(response => content.innerHTML = "<p>Users deleted!</p>")
            .catch ((error) => {
                content.innerHTML = "<p>Database is empty or problem with deleting users!</p>";
            });
}

function deleteOne() {
    content.innerHTML = `<form action="">
                            Write user's you wanna delete phone number<br>
                            <input type="number" id="userPhone">
                            <input type="submit">
                        </form>`;
    content.querySelector("form").addEventListener("submit", function(event) {
        event.preventDefault();
        const phone = document.getElementById("userPhone");
        content.innerHTML = '<p>Deleting user data!</p>';
        fetch(baseUrl + `/users/${phone.value}`, {
                    method: "DELETE"
                })
                .then(response => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    return response;
                })
            .then(response => content.innerHTML = "<p>User deleted!</p>")
            .catch ((error) => {
                content.innerHTML = "<p>Database is empty or problem with deleting user!</p>";
            });
    });
}

makeMenuActionable();
changeContent();
