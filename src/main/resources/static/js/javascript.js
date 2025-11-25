document.addEventListener('DOMContentLoaded', () => {
    // Primero, guardo en constantes todos los elementos del HTML que voy a necesitar.
    const cartSidebar = document.getElementById('carrito');
    const openCartHeaderBtn = document.getElementById('open-cart-btn');
    const closeCartBtn = document.querySelector('.close-cart-btn');
    const cartItemsContainer = document.querySelector('.carrito-items');
    const cartTotalPriceDisplay = document.querySelector('.carrito-precio-total');
    const btnPagar = document.querySelector('.btn-pagar');

    // Elementos del modal de pago.
    const paymentModal = document.getElementById('payment-modal');
    const closePaymentModalBtn = document.getElementById('close-payment-modal');
    const paymentForm = document.getElementById('payment-form');
    const submitPaymentBtn = document.getElementById('submit-payment-btn');
    const paymentErrorDiv = document.getElementById('payment-error');

    // Campos del formulario de la tarjeta.
    const cardNameInput = document.getElementById('card-name');
    const cardNumberInput = document.getElementById('card-number');
    const cardExpiryInput = document.getElementById('card-expiry');
    const cardCvvInput = document.getElementById('card-cvv');

    // Aquí guardaré los productos del carrito y verifico si el usuario ha iniciado sesión.
    let shoppingCart = [];
    const isLoggedIn = typeof globalIsLoggedIn !== 'undefined' ? globalIsLoggedIn : false;

    // ----- LÓGICA DEL CARRITO -----
    // Guardo el carrito en el navegador para que no se borre si refrescan la página.
    const saveCartToLocalStorage = () => localStorage.setItem('shoppingCart', JSON.stringify(shoppingCart));
    // Al cargar la página, intento recuperar el carrito que ya estaba guardado.
    const loadCartFromLocalStorage = () => {
        const storedCart = localStorage.getItem('shoppingCart');
        shoppingCart = storedCart ? JSON.parse(storedCart) : [];
        renderCartItems(); // Y actualizo la vista del carrito.
    };

    // Funciones simples para abrir y cerrar el panel del carrito.
    const openCart = () => cartSidebar.classList.add('open');
    const closeCart = () => cartSidebar.classList.remove('open');

    // Esta función es la que dibuja los productos dentro del carrito.
    const renderCartItems = () => {
        cartItemsContainer.innerHTML = ''; // Limpio el carrito antes de volver a dibujarlo.
        let totalPrice = 0;

        if (shoppingCart.length === 0) {
            cartItemsContainer.innerHTML = '<p class="cart-empty-msg">El carrito está vacío.</p>';
            btnPagar.disabled = true; // No se puede pagar si no hay nada.
        } else {
            btnPagar.disabled = false;
            // Por cada producto en mi array, creo su HTML.
            shoppingCart.forEach(item => {
                const itemTotal = item.price * item.quantity;
                totalPrice += itemTotal;
                const cartItemElement = document.createElement('div');
                cartItemElement.classList.add('carrito-item');
                cartItemElement.dataset.productId = item.productId;
                cartItemElement.innerHTML = `
                    <img src="${item.imageUrl}" alt="${item.name}" class="carrito-item-img">
                    <div class="carrito-item-details">
                        <span class="carrito-item-titulo">${item.name}</span>
                        <div class="selector-cantidad">
                            <i class="fa-solid fa-minus minus-btn"></i>
                            <span class="carrito-item-cantidad">${item.quantity}</span>
                            <i class="fa-solid fa-plus plus-btn"></i>
                        </div>
                        <span class="carrito-item-precio">$${itemTotal.toFixed(2)}</span>
                    </div>
                    <span class="btn-eliminar"><i class="fa-solid fa-trash-can"></i></span>`;
                cartItemsContainer.appendChild(cartItemElement);
            });
        }
        cartTotalPriceDisplay.textContent = `$${totalPrice.toFixed(2)}`; // Actualizo el total.
        saveCartToLocalStorage(); // Guardo cualquier cambio en el LocalStorage.
    };

    // Manejo los clics para sumar, restar o eliminar productos del carrito.
    cartItemsContainer.addEventListener('click', (e) => {
        const cartItemElement = e.target.closest('.carrito-item');
        if (!cartItemElement) return;

        const productId = parseInt(cartItemElement.dataset.productId);
        const itemIndex = shoppingCart.findIndex(item => item.productId === productId);
        if (itemIndex === -1) return;

        if (e.target.matches('.plus-btn, .plus-btn *')) {
            shoppingCart[itemIndex].quantity++;
        } else if (e.target.matches('.minus-btn, .minus-btn *')) {
            shoppingCart[itemIndex].quantity--;
            if (shoppingCart[itemIndex].quantity <= 0) { // Si llega a cero, lo quito.
                shoppingCart.splice(itemIndex, 1);
            }
        } else if (e.target.matches('.btn-eliminar, .btn-eliminar *')) {
            shoppingCart.splice(itemIndex, 1); // Quito el producto directamente.
        }
        renderCartItems(); // Vuelvo a dibujar el carrito con los cambios.
    });

    // Hago esta función global para poder llamarla desde los botones de los productos.
    window.agregarAlCarritoClicked = (event) => {
        const button = event.target.closest('.boton-item, .suggestion-add-btn');
        // Chequeo si el usuario está logueado, si no, lo mando al login.
        if (!isLoggedIn) {
            alert("Debes iniciar sesión para agregar productos al carrito.");
            window.location.href = "/Eccomerce/login";
            return;
        }
        if (!button) return;

        const productId = parseInt(button.dataset.productId);
        const existingItem = shoppingCart.find(item => item.productId === productId);

        if (existingItem) {
            existingItem.quantity++; // Si el producto ya está, solo aumento la cantidad.
        } else {
            // Si es nuevo, lo agrego al array del carrito.
            shoppingCart.push({
                productId: productId,
                name: button.dataset.productName,
                price: parseFloat(button.dataset.productPrice),
                imageUrl: button.dataset.productImage,
                quantity: 1
            });
        }
        renderCartItems(); // Actualizo la vista del carrito.
        openCart(); // Abro el carrito para que vea que se agregó.
    };

    // ----- LÓGICA DEL MODAL DE PAGO -----
    const openPaymentModal = () => {
        paymentModal.style.display = 'flex';
        setTimeout(() => paymentModal.classList.add('visible'), 10); // Pequeña animación de entrada.
    };

    const closePaymentModal = () => {
        paymentModal.classList.remove('visible');
        setTimeout(() => {
            paymentModal.style.display = 'none';
            resetPaymentForm(); // Limpio el formulario al cerrar.
        }, 300);
    };

    // El botón de pagar solo funciona si hay productos y el usuario está logueado.
    btnPagar.addEventListener('click', () => {
        if (shoppingCart.length > 0 && isLoggedIn) {
            openPaymentModal();
        } else if (!isLoggedIn) {
            alert("Debes iniciar sesión para poder pagar.");
            window.location.href = "/Eccomerce/login";
        }
    });

    closePaymentModalBtn.addEventListener('click', closePaymentModal);

    // ----- VALIDACIÓN DEL FORMULARIO -----
    // Funciones para mostrar y ocultar mensajes de error en los campos.
    const showError = (input, message) => {
        input.classList.add('invalid');
        const errorField = document.getElementById(`${input.id}-error`);
        errorField.textContent = message;
    };
    const clearError = (input) => {
        input.classList.remove('invalid');
        const errorField = document.getElementById(`${input.id}-error`);
        errorField.textContent = '';
    };

    // Aquí reviso que todos los campos del formulario de pago estén correctos.
    const validateForm = () => {
        let isValid = true;
        clearError(cardNameInput);
        clearError(cardNumberInput);
        clearError(cardExpiryInput);
        clearError(cardCvvInput);

        if (cardNameInput.value.trim().split(' ').length < 2) {
            showError(cardNameInput, 'Ingrese nombre y apellido.');
            isValid = false;
        }
        if (!/^\d{4}\s\d{4}\s\d{4}\s\d{4}$/.test(cardNumberInput.value)) {
            showError(cardNumberInput, 'Formato inválido. Deben ser 16 dígitos.');
            isValid = false;
        }
        const expiry = cardExpiryInput.value.split('/');
        if (!/^\d{2}\/\d{2}$/.test(cardExpiryInput.value) || expiry[0] > 12 || expiry[0] < 1) {
            showError(cardExpiryInput, 'Formato MM/AA inválido.');
            isValid = false;
        } else { // Reviso si la fecha de la tarjeta no ha expirado.
            const currentYear = new Date().getFullYear() % 100;
            const currentMonth = new Date().getMonth() + 1;
            if (parseInt(expiry[1]) < currentYear || (parseInt(expiry[1]) === currentYear && parseInt(expiry[0]) < currentMonth)) {
                showError(cardExpiryInput, 'La tarjeta ha expirado.');
                isValid = false;
            }
        }
        if (!/^\d{3,4}$/.test(cardCvvInput.value)) {
            showError(cardCvvInput, 'Debe tener 3 o 4 dígitos.');
            isValid = false;
        }
        return isValid;
    };

    // Limpia el formulario y los errores.
    const resetPaymentForm = () => {
        paymentForm.reset();
        clearError(cardNameInput);
        clearError(cardNumberInput);
        clearError(cardExpiryInput);
        clearError(cardCvvInput);
        paymentErrorDiv.style.display = 'none';
        paymentErrorDiv.textContent = '';
        toggleSpinner(false);
    };

    // Muestra u oculta una ruedita de carga en el botón de pagar.
    const toggleSpinner = (show) => {
        const btnText = submitPaymentBtn.querySelector('.btn-text');
        const spinner = submitPaymentBtn.querySelector('.spinner');
        if (show) {
            submitPaymentBtn.disabled = true;
            btnText.style.display = 'none';
            spinner.style.display = 'inline-block';
        } else {
            submitPaymentBtn.disabled = false;
            btnText.style.display = 'inline-block';
            spinner.style.display = 'none';
        }
    };

    // Cuando se envía el formulario de pago...
    paymentForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Evito que la página se recargue.
        if (!validateForm()) return; // Si el formulario es inválido, no hago nada.

        toggleSpinner(true); // Muestro la ruedita de carga.

        try {
            // Envío los datos del carrito a mi API para procesar el pago.
            const response = await fetch('/api/ventas/procesar-pago', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(shoppingCart)
            });

            if (response.ok) {
                alert('¡Compra realizada con éxito! Gracias por confiar en Voltix.');
                shoppingCart = []; // Vacío el carrito.
                renderCartItems(); // Actualizo la vista.
                closePaymentModal(); // Cierro el modal.
                closeCart(); // Cierro el panel del carrito.
            } else {
                // Si el servidor me da un error, lo muestro.
                const errorMessage = await response.text();
                paymentErrorDiv.textContent = `Error: ${errorMessage}`;
                paymentErrorDiv.style.display = 'block';
            }
        } catch (error) {
            paymentErrorDiv.textContent = 'Error de red. Por favor, intente de nuevo.';
            paymentErrorDiv.style.display = 'block';
        } finally {
            toggleSpinner(false); // Quito la ruedita de carga.
        }
    });

    // ----- AUTO-FORMATO DE CAMPOS -----
    // Pongo espacios automáticamente en el número de tarjeta para que sea más fácil de leer.
    cardNumberInput.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim();
    });
    // Y una barrita en la fecha de expiración.
    cardExpiryInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/[^\d]/g, '');
        if (value.length > 2) {
            value = value.slice(0, 2) + '/' + value.slice(2, 4);
        }
        e.target.value = value;
    });

    // ----- LÓGICA DEL MODO OSCURO -----
    const btnTema = document.getElementById('btnTema');

    // Reviso si el usuario ya había elegido un tema y lo aplico.
    const aplicarTema = () => {
        const temaGuardado = localStorage.getItem('tema');
        if (temaGuardado === 'oscuro') {
            document.body.classList.add('oscuro');
            if(btnTema) btnTema.textContent = '☀️';
        } else {
            document.body.classList.remove('oscuro');
            if(btnTema) btnTema.textContent = '🌙';
        }
    };

    // El botón para cambiar de tema.
    if (btnTema) {
        btnTema.addEventListener('click', () => {
            document.body.classList.toggle('oscuro');
            if (document.body.classList.contains('oscuro')) {
                localStorage.setItem('tema', 'oscuro'); // Guardo la preferencia.
                btnTema.textContent = '☀️';
            } else {
                localStorage.setItem('tema', 'claro');
                btnTema.textContent = '🌙';
            }
        });
    }

    // ----- INICIALIZACIÓN -----
    // Asigno los eventos a los botones del carrito.
    if (openCartHeaderBtn) openCartHeaderBtn.addEventListener('click', openCart);
    if (closeCartBtn) closeCartBtn.addEventListener('click', closeCart);

    // Al cargar la página, aplico el tema y cargo el carrito.
    aplicarTema();
    loadCartFromLocalStorage();
});