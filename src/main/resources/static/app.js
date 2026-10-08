async function api(url, options={}) {
    const response = await fetch(url, options);
    const data = await response.json().catch(() => null);
    if (!response.ok) throw new Error(data?.error || "Request failed");
    return data;
}

function esc(value) {
    return String(value ?? "").replace(/[&<>"']/g, c => ({
        "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#039;"
    }[c]));
}

function showMessage(text) {
    const box = document.getElementById("message");
    box.textContent = text;
    box.classList.add("show");
}

async function createPayment() {
    const body = {
        senderUpiId: document.getElementById("sender").value,
        receiverUpiId: document.getElementById("receiver").value,
        amount: Number(document.getElementById("amount").value),
        note: document.getElementById("note").value
    };
    try {
        const result = await api("/api/payments/offline", {
            method: "POST",
            headers: {"Content-Type":"application/json"},
            body: JSON.stringify(body)
        });
        showMessage(`${result.transactionId} queued successfully. Run gossip, then click Sync.`);
        await loadAll();
    } catch (e) {
        showMessage(e.message);
    }
}

async function syncPayment(id) {
    try {
        const result = await api(`/api/payments/${encodeURIComponent(id)}/sync`, {method:"POST"});
        showMessage(`${result.transactionId}: ${result.status} — ${result.message}`);
        await loadAll();
    } catch(e) { showMessage(e.message); }
}

async function gossip() {
    try {
        const response = await fetch("/api/mesh/gossip", {method:"POST"});
        showMessage(await response.text());
        await loadAll();
    } catch(e) { showMessage(e.message); }
}

async function loadAll() {
    try {
        const [accounts,payments,packets] = await Promise.all([
            api("/api/accounts"), api("/api/payments"), api("/api/mesh/packets")
        ]);

        document.getElementById("accountCount").textContent = accounts.length;
        document.getElementById("paymentCount").textContent = payments.length;
        document.getElementById("packetCount").textContent = packets.length;

        document.getElementById("accounts").innerHTML = accounts.length ? `
            <table><thead><tr><th>UPI ID</th><th>Name</th><th>Balance</th></tr></thead>
            <tbody>${accounts.map(a=>`<tr><td>${esc(a.upiId)}</td><td>${esc(a.name)}</td><td>₹${Number(a.balance).toFixed(2)}</td></tr>`).join("")}</tbody></table>` : '<div class="empty">No accounts.</div>';

        document.getElementById("payments").innerHTML = payments.length ? `
            <table><thead><tr><th>Transaction</th><th>Route</th><th>Amount</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>${payments.map(p=>`<tr>
                <td>${esc(p.transactionId)}</td>
                <td>${esc(p.senderUpiId)} → ${esc(p.receiverUpiId)}</td>
                <td>₹${Number(p.amount).toFixed(2)}</td>
                <td class="status ${String(p.status).toLowerCase()}">${esc(p.status)}</td>
                <td>${p.status === "QUEUED_OFFLINE" ? `<button onclick="syncPayment('${esc(p.transactionId)}')">Sync</button>` : "—"}</td>
            </tr>`).join("")}</tbody></table>` : '<div class="empty">No payments yet.</div>';

        document.getElementById("packets").innerHTML = packets.length ? `
            <table><thead><tr><th>Packet</th><th>Transaction</th><th>Hops</th><th>TTL</th></tr></thead>
            <tbody>${packets.map(p=>`<tr><td>${esc(p.packetId)}</td><td>${esc(p.transactionId)}</td><td>${p.hops}</td><td>${p.ttl}</td></tr>`).join("")}</tbody></table>` : '<div class="empty">No mesh packets yet.</div>';
    } catch(e) {
        showMessage(e.message);
    }
}

loadAll();
setInterval(loadAll, 5000);
