import './globals.css'
import type {Metadata} from 'next'
import React from "react";

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
      <html lang="en" className="h-full">
      <body className="min-h-full bg-gray-900 text-white">{children}</body>
    </html>
  )
}
