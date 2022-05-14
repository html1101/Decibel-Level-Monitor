const xhttp = new XMLHttpRequest();

// Listen for click on add class button
document.getElementById("add_class").addEventListener("click", _evt => {
    // On click, show a popup asking what the class is called and when it is.
    document.getElementById("add_class_inp").style.display = "table-row";

    // Add key event for if enter is pressed
    window.onkeyup = evt => {
        if(evt.key == "Enter") {
            // Add class
            let class_name = document.querySelector("#add_class_inp .class_name").value,
            time_1 = document.querySelector("#add_class_inp .t1").value,
            time_2 = document.querySelector("#add_class_inp .t2").value;
            
            // Convert times to the formatting Profile.java expects (add seconds).
            time_1 = time_1.padStart(4, "0");
            time_2 = time_2.padStart(4, "0");

            // Send POST request asking to add this class to our profile. Then reload.
            xhttp.open("POST", "/addClass");
            xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            xhttp.send(JSON.stringify({
                className: class_name,
                start: time_1,
                end: time_2
            }));
        }
    }
});