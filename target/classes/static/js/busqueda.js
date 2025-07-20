// Espero a que todo el HTML esté cargado antes de ejecutar mi código.
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('search-input');
    const suggestionsContainer = document.getElementById('search-suggestions');

    // Me aseguro de que el buscador y su contenedor de sugerencias existan para evitar errores.
    if (searchInput && suggestionsContainer) {
        // Cada vez que el usuario escribe algo en el buscador, ejecuto esta función.
        searchInput.addEventListener('input', function () {
            const term = this.value.trim(); // Obtengo el texto y quito espacios inútiles.

            // Si el texto es muy corto, no busco nada para no sobrecargar el servidor.
            if (term.length < 2) {
                suggestionsContainer.innerHTML = '';
                suggestionsContainer.style.display = 'none';
                return;
            }

            // Hago una llamada a mi API para buscar productos que coincidan.
            fetch(`/api/productos/buscar?term=${encodeURIComponent(term)}`)
                .then(response => response.json()) // Convierto la respuesta a JSON.
                .then(data => {
                    suggestionsContainer.innerHTML = ''; // Limpio las sugerencias anteriores.
                    if (data.length > 0) {
                        // Si encontré resultados, los recorro y creo un elemento por cada uno.
                        data.forEach(producto => {
                            const suggestionItem = document.createElement('div');
                            suggestionItem.className = 'suggestion-item';

                            // Construyo el HTML para cada sugerencia, con su imagen, nombre y botón para agregar al carrito.
                            suggestionItem.innerHTML = `
                                <a href="/buscar?q=${encodeURIComponent(producto.nombre)}" class="suggestion-item-info">
                                    <img src="${producto.imagenUrl}" alt="${producto.nombre}">
                                    <span>${producto.nombre}</span>
                                </a>
                                <button class="boton-item suggestion-add-btn"
                                        onclick="agregarAlCarritoClicked(event)"
                                        data-product-id="${producto.idProducto}"
                                        data-product-name="${producto.nombre}"
                                        data-product-price="${producto.precio}"
                                        data-product-image="${producto.imagenUrl}">
                                    <i class="fa-solid fa-plus"></i>
                                </button>
                            `;
                            suggestionsContainer.appendChild(suggestionItem);
                        });
                        suggestionsContainer.style.display = 'block'; // Muestro el contenedor de sugerencias.
                    } else {
                        suggestionsContainer.style.display = 'none'; // Si no hay resultados, lo oculto.
                    }
                })
                .catch(error => {
                    // Si algo falla en la búsqueda, lo muestro en la consola y oculto las sugerencias.
                    console.error('Error al buscar sugerencias:', error);
                    suggestionsContainer.style.display = 'none';
                });
        });

        // Si hago clic en cualquier otra parte de la página, oculto las sugerencias.
        document.addEventListener('click', function (e) {
            if (!searchInput.contains(e.target) && !suggestionsContainer.contains(e.target)) {
                suggestionsContainer.style.display = 'none';
            }
        });
    }
});