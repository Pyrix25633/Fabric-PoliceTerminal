export class RequireNonNull {
    static getElementById(id) {
        const element = document.getElementById(id);
        if (element != null)
            return element;
        throw new Error('No element found with id: ' + id);
    }
}
