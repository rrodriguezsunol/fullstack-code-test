const serviceListContainer = document.querySelector('#service-list');
let servicesRequest = new Request('/service');
fetch(servicesRequest)
    .then(function (response) {
        return response.json();
    })
    .then(function (serviceList) {
        serviceList.forEach(service => {
            let tableRow = document.createElement("tr");

            let serviceNameColumn = document.createElement("td");
            serviceNameColumn.appendChild(document.createTextNode(service.name));

            let dateCreatedColumn = document.createElement("td");
            dateCreatedColumn.appendChild(document.createTextNode(new Intl.DateTimeFormat('en-gb', {dateStyle: 'medium', timeStyle: 'medium'})
                .format(Date.parse(service.createdAt))));

            let statusColumn = document.createElement("td");
            statusColumn.appendChild(document.createTextNode(service.status));

            let deleteButton = document.createElement("button");
            deleteButton.appendChild(document.createTextNode("Delete"));
            deleteButton.setAttribute("id", `delete-service-${service.name}`);
            deleteButton.onclick = evt => {
                fetch(`/service/${service.name}`, {
                    method: 'delete',
                    headers: {
                        'Accept': 'application/json'
                    }
                }).then(res => location.reload());
            };

            tableRow.append(serviceNameColumn, dateCreatedColumn, statusColumn, deleteButton);
            serviceListContainer.appendChild(tableRow);
        });
    });

const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
    let serviceName = document.querySelector('#service-name').value;
    let serviceUrl = document.querySelector('#service-url').value;

    fetch('/service', {
        method: 'post',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: serviceName, url: serviceUrl})
    }).then(res => location.reload());
};