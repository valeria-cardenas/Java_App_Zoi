document.querySelectorAll(".form-error, .error-flash").forEach((alert) => {
  alert.setAttribute("role", "alert");
  alert.setAttribute("aria-live", "assertive");
});
document.querySelectorAll(".success-flash").forEach((alert) => {
  alert.setAttribute("role", "status");
  alert.setAttribute("aria-live", "polite");
});

document.querySelectorAll("form").forEach((form) => {
  const hasUserFields = form.querySelector(
    'input:not([type="hidden"]):not([type="submit"]), select, textarea',
  );
  if (!hasUserFields || form.method.toLowerCase() === "get") return;
  const summary = document.createElement("div");
  summary.className = "form-error validation-summary";
  summary.hidden = true;
  form.prepend(summary);
  form.querySelectorAll("input, select, textarea").forEach((field) => {
    const message = field.dataset.error;
    if (!message) return;
    field.addEventListener("invalid", () => {
      field.setCustomValidity(
        field.validity.valueMissing ? "Este campo es obligatorio." : message,
      );
    });
    field.addEventListener("input", () => field.setCustomValidity(""));
    field.addEventListener("change", () => field.setCustomValidity(""));
  });

  form
    .querySelectorAll('input[type="file"][data-max-bytes]')
    .forEach((field) => {
      field.addEventListener("change", () => {
        const file = field.files[0];
        if (!file) return;
        const maxBytes = Number(field.dataset.maxBytes);
        const allowed = (field.dataset.fileTypes || "")
          .split(",")
          .filter(Boolean);
        if (file.size > maxBytes) {
          field.setCustomValidity(
            field.dataset.sizeError || "El archivo supera el tamaño permitido.",
          );
        } else if (allowed.length && !allowed.includes(file.type)) {
          field.setCustomValidity(
            field.dataset.error || "El formato del archivo no está permitido.",
          );
        } else {
          field.setCustomValidity("");
        }
        field.reportValidity();
      });
    });

  form.querySelectorAll("[data-match]").forEach((field) => {
    const original = form.querySelector(`[name="${field.dataset.match}"]`);
    const validarCoincidencia = () => {
      field.setCustomValidity(
        field.value && original && field.value !== original.value
          ? field.dataset.matchError || "Los valores no coinciden."
          : "",
      );
    };
    field.addEventListener("input", validarCoincidencia);
    original?.addEventListener("input", validarCoincidencia);
  });

  form.addEventListener("submit", (event) => {
    if (!form.checkValidity()) {
      event.preventDefault();
      summary.textContent = "Revisa los campos señalados antes de continuar.";
      summary.hidden = false;
      form.querySelector(":invalid")?.reportValidity();
      return;
    }
    if (form.hasAttribute("data-confirm-empty")) {
      const vacios = [
        ...form.querySelectorAll(
          "input:not([type=file]):not([type=hidden]), textarea, select",
        ),
      ]
        .filter((field) => !field.required && !String(field.value || "").trim())
        .map((field) =>
          field.closest("label")?.childNodes[0]?.textContent.trim(),
        )
        .filter(Boolean);
      if (
        vacios.length &&
        !window.confirm(
          `Los siguientes campos opcionales están vacíos:\n\n${vacios.join(", ")}\n\n¿Deseas guardar la mascota de todas formas?`,
        )
      ) {
        event.preventDefault();
      }
    }
  });
});
