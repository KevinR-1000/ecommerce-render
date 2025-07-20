document.addEventListener('DOMContentLoaded', () => {

    // --- MANEJO DE ALERTAS ---
    // Tomo los mensajes que me mandó el controlador a través de atributos en el body.
    const body = document.body;
    const successMessage = body.dataset.successMessage;
    const errorMessage = body.dataset.errorMessage;
    const infoMessage = body.dataset.infoMessage;

    // Si hay un mensaje de éxito (ej. registro correcto), muestro una alerta bonita que se cierra sola.
    if (successMessage) {
        Swal.fire({
            icon: 'success',
            title: successMessage,
            showConfirmButton: false,
            timer: 2000
        });
    }

    // Si hay un mensaje de error (ej. contraseña incorrecta), muestro una alerta de error.
    if (errorMessage) {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: errorMessage,
            confirmButtonColor: '#003366'
        });
    }

    // Si hay un mensaje de información (ej. logout), muestro una alerta informativa.
    if (infoMessage) {
        Swal.fire({
            icon: 'info',
            title: infoMessage,
            showConfirmButton: false,
            timer: 2000
        });
    }


    // --- ANIMACIÓN DEL FORMULARIO DE LOGIN/REGISTRO ---
    // Esto es solo para el efecto visual de deslizar los paneles.
    const container = document.getElementById('container');
    const registerBtn = document.getElementById('register');
    const loginBtn = document.getElementById('login');

    if (container && registerBtn && loginBtn) {
        // Cuando hago clic en "Registrarse", agrego la clase 'active' para que se mueva.
        registerBtn.addEventListener('click', () => {
            container.classList.add("active");
        });

        // Cuando hago clic en "Iniciar sesión", quito la clase para que vuelva a su lugar.
        loginBtn.addEventListener('click', () => {
            container.classList.remove("active");
        });
    }
});