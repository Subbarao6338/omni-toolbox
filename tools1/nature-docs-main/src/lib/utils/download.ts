export function downloadFile(name: string, content: string | ArrayBuffer, type: string) {
  let blob: Blob;

  if (content instanceof ArrayBuffer) {
    blob = new Blob([content], { type });
  } else if (typeof content === 'string') {
    if (content.startsWith('data:')) {
      // It's a data URL, let's extract the actual content if possible or just use it as is
      const link = document.createElement('a');
      link.href = content;
      link.download = name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      return;
    }
    blob = new Blob([content], { type });
  } else {
    console.error('Unsupported content type for download');
    return;
  }

  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = name;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
