import { pdf as pdfApi } from `../api/apiClient`;

export async function downloadPDF(blobResponse, filename) {
  const url = window.URL.createObjectURL(new Blob([blobResponse]));
  const link = document.createElement(`a`);
  link.href = url;
  link.setAttribute(`download`, filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

// usage
const handleDownloadBill = async (id) => {
  try {
    const res = await pdfApi.downloadBill(id); // response.data is a blob
    downloadPDF(res.data, `bill_${id}.pdf`);
  } catch (err) {
    console.error(`Download failed`, err);
  }
};
