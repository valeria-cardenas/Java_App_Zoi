document.addEventListener('DOMContentLoaded', () => {
    const messages = document.getElementById('chatMessages');
    const input = document.getElementById('chatImage');
    const preview = document.getElementById('attachmentPreview');
    const image = document.getElementById('attachmentImage');
    const name = document.getElementById('attachmentName');
    const remove = document.getElementById('removeAttachment');
    const textarea = document.querySelector('.composer textarea');
    const composer = document.querySelector('.composer');
    if (messages) messages.scrollTop = messages.scrollHeight;

    const clear = () => {
        input.value = ''; preview.hidden = true;
        image.removeAttribute('src'); name.textContent = '';
    };
    input?.addEventListener('change', () => {
        const file = input.files?.[0];
        if (!file) return clear();
        name.textContent = file.name;
        image.src = URL.createObjectURL(file);
        preview.hidden = false;
    });
    remove?.addEventListener('click', clear);
    textarea?.addEventListener('input', () => {
        textarea.style.height = 'auto';
        textarea.style.height = `${Math.min(textarea.scrollHeight, 140)}px`;
    });

    if (composer && document.documentElement.classList.contains('viewer-vet')) {
        const organizer = document.createElement('form');
        organizer.method = 'post';
        organizer.action = composer.action.replace('/mensajes', '/organizar');
        organizer.className = 'chat-organizer';
        const csrf = composer.querySelector('input[type="hidden"]')?.cloneNode(true);
        if (csrf) organizer.appendChild(csrf);
        organizer.insertAdjacentHTML('beforeend', `
            <label>Estado<select name="estado">
                <option value="PENDIENTE">Pendiente</option><option value="RESPONDIDA">Respondida</option><option value="CERRADA">Cerrada</option>
            </select></label>
            <label>Prioridad<select name="prioridad">
                <option value="NORMAL">Normal</option><option value="IMPORTANTE">Importante</option><option value="URGENTE">Urgente</option>
            </select></label><button type="submit">Guardar</button>`);
        organizer.querySelector('[name="estado"]').value = document.documentElement.dataset.chatState || 'PENDIENTE';
        organizer.querySelector('[name="prioridad"]').value = document.documentElement.dataset.chatPriority || 'NORMAL';
        document.querySelector('.conversation-header')?.appendChild(organizer);

        const initialCount = messages?.querySelectorAll('.message').length || 0;
        const activityUrl = composer.action.replace('/mensajes', '/actividad');
        const updateReadMarks = (unread) => {
            const selector = document.documentElement.classList.contains('viewer-owner') ? '.message.owner' : '.message.vet';
            const ownMessages = [...document.querySelectorAll(selector)];
            ownMessages.forEach((message, index) => {
                let mark = message.querySelector('.read-mark');
                if (!mark) {
                    mark = document.createElement('span'); mark.className = 'read-mark';
                    message.querySelector('time')?.appendChild(mark);
                }
                mark.textContent = unread === 0 || index < ownMessages.length - unread ? ' ✓✓' : ' ✓';
                mark.title = mark.textContent.includes('✓✓') ? 'Leído' : 'Enviado';
            });
        };
        const checkActivity = async () => {
            if (document.hidden || textarea?.value.trim() || input?.files?.length) return;
            try {
                const response = await fetch(activityUrl, {headers: {'Accept': 'application/json'}});
                if (!response.ok) return;
                const activity = await response.json();
                updateReadMarks(activity.propiosSinLeer);
                if (activity.mensajes !== initialCount) location.reload();
            } catch (_) {
                // Una pérdida temporal de conexión no debe interrumpir lo que el usuario escribe.
            }
        };
        checkActivity();
        window.setInterval(checkActivity, 6000);
    }

    if (composer && document.documentElement.classList.contains('viewer-owner')) {
        const initialCount = messages?.querySelectorAll('.message').length || 0;
        const activityUrl = composer.action.replace('/mensajes', '/actividad');
        window.setInterval(async () => {
            if (document.hidden || textarea?.value.trim() || input?.files?.length) return;
            try {
                const response = await fetch(activityUrl, {headers: {'Accept': 'application/json'}});
                if (response.ok && (await response.json()).mensajes !== initialCount) location.reload();
            } catch (_) {}
        }, 6000);
    }
});
