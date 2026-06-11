/**
 * 下载工具类
 */

/**
 * 下载 Blob 文件
 * @param blob 文件 Blob 数据
 * @param filename 下载文件名
 */
export function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/**
 * 通过 URL 下载文件
 * @param url 文件下载 URL
 * @param filename 下载文件名
 */
export function downloadByUrl(url: string, filename?: string) {
  const link = document.createElement('a')
  link.href = url
  if (filename) {
    link.download = filename
  }
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
