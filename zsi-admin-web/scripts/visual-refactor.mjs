import fs from 'fs';
import path from 'path';

const root = path.resolve('src/app/features');

const replacements = [
  ['table table-zebra bg-base-100 shadow-lg rounded-box', 'table table-zebra w-full'],
  ['<div class="overflow-x-auto">', '<div class="bh-page overflow-x-auto">'],
  ['max-w-2xl mx-auto bg-base-100 shadow-xl rounded-box p-6', 'bh-form-card p-6 md:p-8'],
  ['max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6', 'bh-form-card p-6 md:p-8'],
  ['text-center py-8 text-gray-500', 'bh-empty-state'],
  ['class="btn btn-primary btn-sm"', 'class="bh-btn-primary"'],
  ['<h2 class="text-2xl font-bold">', '<h2 class="bh-page-title">'],
];

function walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) walk(full);
    else if (entry.name.endsWith('.ts')) {
      let content = fs.readFileSync(full, 'utf8');
      const original = content;
      for (const [from, to] of replacements) {
        content = content.split(from).join(to);
      }
      if (content !== original) fs.writeFileSync(full, content);
    }
  }
}

walk(root);
console.log('Visual refactor replacements applied.');
