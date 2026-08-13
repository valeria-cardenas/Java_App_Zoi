document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.account-delete-form').forEach(form => {
        form.addEventListener('submit', event => {
            const nombre = form.dataset.userName || 'esta cuenta';
            const confirmado = window.confirm(
                `¿Eliminar definitivamente la cuenta de ${nombre}?\n\n` +
                'Se borrarán todos sus datos asociados. Esta acción no se puede deshacer.'
            );
            if (!confirmado) event.preventDefault();
        });
    });
});
if (!document.querySelector('script[src*="zoi-validation.js"]')) {
    const validationScript = document.createElement('script');
    validationScript.src = '/js/zoi-validation.js';
    document.head.appendChild(validationScript);
}
