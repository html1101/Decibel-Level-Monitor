const xhttp = new XMLHttpRequest();

// Seed generator, see https://stackoverflow.com/questions/521295/seeding-the-random-number-generator-in-javascript
let m_w = 123456789, m_z = 987654321, mask = 0xffffffff;

// Takes any integer
function seed(i) {
    m_w = (123456789 + i) & mask;
    m_z = (987654321 - i) & mask;
}

// Returns number between 0 (inclusive) and 1.0 (exclusive),
// just like Math.random().
function random()
{
    m_z = (36969 * (m_z & 65535) + (m_z >> 16)) & mask;
    m_w = (18000 * (m_w & 65535) + (m_w >> 16)) & mask;
    let result = ((m_z << 16) + (m_w & 65535)) >>> 0;
    result /= 4294967296;
    return result;
}

// Listen for click on add class button
document.getElementById("add_class").addEventListener("click", _evt => {
    // On click, show a popup asking what the class is called and when it is.
    let add_class = document.getElementById("add_class_inp");
    add_class.style.display = "block";

    // If add_class_inp is presed, close out popup
    add_class.onclick = function(e) {
        // Check we click the gray part, not the actual #class_inp
        if(this == e.target) {
            add_class.style.display = "none";
        }
    }

    // Add key event for if .add is pressed
    add_class.querySelector(".add").onclick = _evt => {
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

        setTimeout(() => window.location.reload(), 500);
    }
});

// Go through all .imgs and create color for them
document.querySelectorAll(".img").forEach(col_elem => {
    col_elem.innerText = col_elem.innerText.toUpperCase();

    // Create color based on ascii symbol
    let to_ascii = col_elem.innerText.charCodeAt(0);
    seed(to_ascii);
    // console.log(random(), random() *255, to_ascii);
    col_elem.style.background = "hsl(" + random()*255 + ", 80%, 60%)";
})