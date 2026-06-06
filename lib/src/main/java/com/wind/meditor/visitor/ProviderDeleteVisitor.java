package com.wind.meditor.visitor;

import com.wind.meditor.utils.NodeValue;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

class ProviderDeleteVisitor extends NodeVisitor {

    private final NodeVisitor parentWriter;
    private final List<String> deleteAuthorities;
    private final String elementNs;
    private final String elementName;

    private final List<AttrRecord> pendingAttrs = new ArrayList<>();

    private NodeVisitor writerNode;
    private boolean shouldDelete;
    private boolean decided;
    private int lineNumber = -1;

    private static class AttrRecord {
        final String ns;
        final String name;
        final int resourceId;
        final int type;
        final Object obj;

        AttrRecord(String ns, String name, int resourceId, int type, Object obj) {
            this.ns = ns;
            this.name = name;
            this.resourceId = resourceId;
            this.type = type;
            this.obj = obj;
        }
    }

    ProviderDeleteVisitor(NodeVisitor parentWriter, List<String> deleteAuthorities, String ns, String name) {
        this.parentWriter = parentWriter;
        this.deleteAuthorities = deleteAuthorities;
        this.elementNs = ns;
        this.elementName = name;
    }

    @Override
    public void line(int ln) {
        lineNumber = ln;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (decided && shouldDelete) {
            return;
        }

        if (!decided && NodeValue.Application.Provider.AUTHORITIES.equals(name) && obj instanceof String) {
            shouldDelete = isAuthorityMatched((String) obj);
            decided = true;

            if (shouldDelete) {
                pendingAttrs.clear();
                return;
            }

            ensureWriterCreated();
            flushPendingAttrs();
            writerNode.attr(ns, name, resourceId, type, obj);
            return;
        }

        if (!decided) {
            pendingAttrs.add(new AttrRecord(ns, name, resourceId, type, obj));
            return;
        }

        ensureWriterCreated();
        writerNode.attr(ns, name, resourceId, type, obj);
    }

    @Override
    public NodeVisitor child(String ns, String name) {
        if (!decided) {
            shouldDelete = false;
            decided = true;
            ensureWriterCreated();
            flushPendingAttrs();
        }

        if (shouldDelete) {
            return null;
        }
        return writerNode == null ? null : writerNode.child(ns, name);
    }

    @Override
    public void end() {
        if (!decided) {
            shouldDelete = false;
            decided = true;
            ensureWriterCreated();
            flushPendingAttrs();
        }

        if (!shouldDelete && writerNode != null) {
            writerNode.end();
        }
    }

    private boolean isAuthorityMatched(String authorities) {
        for (String deleteAuthority : deleteAuthorities) {
            if (deleteAuthority.equals(authorities)) {
                return true;
            }
        }
        return false;
    }

    private void ensureWriterCreated() {
        if (writerNode == null) {
            writerNode = parentWriter.child(elementNs, elementName);
            if (lineNumber >= 0) {
                writerNode.line(lineNumber);
            }
        }
    }

    private void flushPendingAttrs() {
        for (AttrRecord pendingAttr : pendingAttrs) {
            writerNode.attr(pendingAttr.ns, pendingAttr.name, pendingAttr.resourceId, pendingAttr.type, pendingAttr.obj);
        }
        pendingAttrs.clear();
    }
}
