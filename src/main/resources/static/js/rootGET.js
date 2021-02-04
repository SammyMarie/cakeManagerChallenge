const baseUrl = "http://localhost:8282";

function loadCakes() {
    fetch(baseUrl)
        .then(response => response.json())
        .then(cakes => {

            let cakeTableTop = `<table><thead>
                                        <tr>
                                            <th>Cake_Id</th>
                                            <th>Title</th>
                                            <th>Description</th>
                                            <th>Image</th>
                                        </tr>
                                        </thead>`

            let main = "";
            for (let index = 0; index < cakes.length; index++) {
                main += "<tbody>" +
                    "<tr><td>" + cakes[index].cakeId + "</td><td>"
                    + cakes[index].title + "</td><td>" + cakes[index].desc + "</td><td>"
                    + cakes[index].image + "</td></tr>" +
                    "</tbody>";
            }

            let cakeTableBottom = `</table>`
            document.getElementById("cakeInfo").innerHTML = cakeTableTop + main + cakeTableBottom;
        });
}

window.onload = function () {
    loadCakes();
}