import './globals.css'
import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Flag Quiz Game',
  description: 'Test your knowledge of world flags!',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  )
}