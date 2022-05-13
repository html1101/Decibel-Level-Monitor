const xhttp = new XMLHttpRequest();

// Listen for click on add class button
document.getElementById("add_class").addEventListener("click", _evt => {
    // On click, show a popup asking what the class is called and when it is.
    document.getElementById("add_class_inp").style.display = "table-row";

    // Add key event for if enter is pressed
    window.onkeyup = evt => {
        if(evt.key == "Enter") {
            // Add class
            let class_name = document.querySelector("#add_class_inp > .class_name"),
            time_1 = document.querySelector("#add_class_inp > .t1"),
            time_2 = document.querySelector("#add_class_inp > .t2");

            // Send POST request asking to add this class to our profile. Then reload.
            xhttp.open("POST", "/addClass");
            xhttp.send({
                className: class_name,
                starts: time_1,
                ends: time_2
            });
        }
    }
});